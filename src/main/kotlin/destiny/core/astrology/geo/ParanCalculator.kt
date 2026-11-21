/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Aspect
import destiny.core.astrology.EquatorialPos
import destiny.tools.CircleTools.normalizeSigned
import jakarta.inject.Named
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.tan

/**
 * [IParanCalculator] 的實作，純數學（只用 context 裡的赤經赤緯）。
 *
 * 記號：α 赤經、δ 赤緯、φ 緯度、D(φ, δ) = acos(−tan φ · tan δ) 半日弧。
 * 星體在角上時的時角 HA：MC＝0、IC＝180、ASC＝−D、DSC＝+D。兩顆星同時到角 ⇔ `α_A + HA_A(φ) ≡ α_B + HA_B(φ)`。
 *
 * - 一顆在 MC／IC、一顆在 ASC／DSC：另一顆的時角已知，D 已知 → `tan φ = −cos D / tan δ`，一次 atan。
 * - 兩顆都在 MC／IC：條件與緯度無關，不成線，回空。
 * - 兩顆都在 ASC／DSC、以及非合衝刑的世俗相位：在緯度上取樣找變號，再二分。
 * - 有移動星（[AstroGeoContext.tracks]，月亮）參與時，[refine]／[curve] 依經度精算：同樣對緯度求根，只是星的位置取到角那一刻的。
 */
@Named
class ParanCalculator : IParanCalculator {

  override fun parans(ctx: AstroGeoContext, query: ParanQuery): List<Paran> {
    val angular = query.aspects.filter { it in Aspect.PARAN }
    val mundane = query.aspects.filterNot { it in Aspect.PARAN }
    val done = mutableSetOf<Set<AstroPoint>>()
    val result = mutableListOf<Paran>()

    for (anchor in query.anchors) {
      val posA = ctx.equatorial[anchor]?.get(query.basis) ?: continue
      for (partner in query.partners) {
        if (partner == anchor) continue
        val posB = ctx.equatorial[partner]?.get(query.basis) ?: continue

        if (angular.isNotEmpty() && done.add(setOf(anchor, partner))) {
          for (a in query.angles) for (aspect in angular) for (b in partnerAngles(a, aspect)) {
            angularLats(posA, a, posB, b).forEach { result += Paran(anchor, a, partner, aspect, b, it) }
          }
        }
        for (a in query.angles) for (aspect in mundane) for (offset in offsets(aspect)) {
          mundaneLats(posA, a, posB, offset).forEach { result += Paran(anchor, a, partner, aspect, null, it) }
        }
      }
    }
    return result
  }

  override fun refine(ctx: AstroGeoContext, paran: Paran, basis: AstroGeoBasis, lng: Double): Paran? {
    val m = Moving.of(ctx, paran, basis) ?: return paran
    return m.solve(lng, paran.lat, WIDE_WINDOW)?.let { paran.copy(lat = it) }
  }

  override fun curve(ctx: AstroGeoContext, paran: Paran, basis: AstroGeoBasis, lngs: List<Double>): List<Double?>? {
    val m = Moving.of(ctx, paran, basis) ?: return null
    var prev: Double? = null
    return lngs.map { lng ->
      // 沿經度延續上一點的解（同一支）；接不上（跳到另一天、或那一段不升落）再從靜態的緯度重找
      val lat = prev?.let { m.solve(lng, it, NARROW_WINDOW) } ?: m.solve(lng, paran.lat, WIDE_WINDOW)
      prev = lat
      lat
    }
  }

  /**
   * 一條有移動星參與的 paran 在某條經線上的解。
   * 未知數只有緯度 φ：給定 φ，anchor 在這條經線上到角的時刻 t(φ) 就定了（離出生最近的那一次），
   * 兩顆星都取 t 的位置，看 partner 是否也在該在的角（或世俗位置）—— 對 φ 求根。
   */
  private class Moving(
    private val ctx: AstroGeoContext,
    private val paran: Paran,
    private val basis: AstroGeoBasis,
    private val staticA: EquatorialPos,
    private val staticB: EquatorialPos,
  ) {
    private val trackA = ctx.tracks[paran.anchor]
    private val trackB = ctx.tracks[paran.partner]
    private val jd0 = ctx.gmtJulDay.value

    private fun posA(dt: Double) = trackA?.at(jd0 + dt, basis) ?: staticA
    private fun posB(dt: Double) = trackB?.at(jd0 + dt, basis) ?: staticB

    /** 傳回 (F, Δt)；F 是 partner 離「該在的位置」差幾度（−180..180），φ 無定義時 F 為 NaN */
    private fun eval(lat: Double, lng: Double, target: Double?): DoubleArray {
      val tanPhi = tan(Math.toRadians(lat))
      var pa = posA(0.0)
      var dt = 0.0
      var haA = Double.NaN
      // anchor 本身在動（月亮當 anchor）時，到角時刻與它的位置互相依賴：迭代四次就收斂（月亮一小時 0.5°）
      // 哪一天由第一次決定（離出生最近的那一次），之後固定同一天 —— 否則在「離出生半天」附近會在前後兩天之間來回跳
      repeat(if (trackA != null) 4 else 1) { i ->
        haA = hourAngle(paran.anchorAngle, tanPhi, tan(Math.toRadians(pa.declination)))
        if (haA.isNaN()) return doubleArrayOf(Double.NaN, 0.0)
        val raw = pa.rightAscension + haA - ctx.gmstDeg - lng
        dt = if (i == 0) signed(raw) / SIDEREAL_DEG_PER_DAY
        else (dt * SIDEREAL_DEG_PER_DAY).let { prev -> (prev + signed(raw - prev)) / SIDEREAL_DEG_PER_DAY }
        pa = posA(dt)
      }
      val pb = posB(dt)
      val tanB = tan(Math.toRadians(pb.declination))
      val f = if (target == null) {
        signed(pa.rightAscension + haA - pb.rightAscension - hourAngle(paran.partnerAngle!!, tanPhi, tanB))
      } else {
        val dsa = semiArc(tanPhi, tanB)
        if (dsa > 0.0 && dsa < 180.0) signed(mundanePosition(signed(pa.rightAscension + haA - pb.rightAscension), dsa) - target) else Double.NaN
      }
      return doubleArrayOf(f, dt)
    }

    /** 在 [seed] ± [window] 內找根，取最近 [seed] 的；合衝刑之外的世俗相位兩個方向都試 */
    fun solve(lng: Double, seed: Double, window: Double): Double? {
      val targets: List<Double?> = if (paran.partnerAngle != null) listOf(null) else offsets(paran.aspect).map { mundaneOf(paran.anchorAngle) + it }
      // 先從起點試割線法（通常 4–6 次；靠近「不升落」邊界的根，等距取樣容易漏掉），失敗再掃描
      targets.mapNotNull { secant(lng, seed, window, it) }.minByOrNull { (it - seed).absoluteValue }?.let { return it }
      return targets.flatMap { target -> roots(lng, seed, window, target) }.minByOrNull { (it - seed).absoluteValue }
    }

    private fun secant(lng: Double, seed: Double, window: Double, target: Double?): Double? {
      var x0 = seed
      var e0 = eval(x0, lng, target)
      var x1 = seed + SECANT_DELTA
      var e1 = eval(x1, lng, target)
      repeat(SECANT_ROUNDS) {
        if (e0[0].isNaN() || e1[0].isNaN() || (e1[1] - e0[1]).absoluteValue > 0.25) return null
        val d = e1[0] - e0[0]
        if (d == 0.0) return null
        val x2 = x1 - e1[0] * (x1 - x0) / d
        if ((x2 - seed).absoluteValue > window || x2.absoluteValue > MAX_LAT) return null
        x0 = x1; e0 = e1
        x1 = x2; e1 = eval(x2, lng, target)
        if ((x1 - x0).absoluteValue < 1e-7) return x1.takeIf { !e1[0].isNaN() && e1[0].absoluteValue < 1e-5 }
      }
      return null
    }

    private fun roots(lng: Double, seed: Double, window: Double, target: Double?): List<Double> {
      val lo = maxOf(seed - window, -MAX_LAT)
      val hi = minOf(seed + window, MAX_LAT)
      val n = ((hi - lo) / (if (window <= NARROW_WINDOW) NARROW_STEP else WIDE_STEP)).toInt().coerceAtLeast(2)
      return scan(lng, target, lo, hi, n, 2)
    }

    /**
     * [lo]..[hi] 等分 [n] 段找變號再二分。一端有值、一端無定義（星在那裡開始不升落）的那一段再細分（最多兩層，約 0.0006°）——
     * 根常常就貼在那條邊界旁
     */
    private fun scan(lng: Double, target: Double?, lo: Double, hi: Double, n: Int, depth: Int): List<Double> {
      val step = (hi - lo) / n
      val result = mutableListOf<Double>()
      var prevLat = lo
      var prev = eval(lo, lng, target)
      for (i in 1..n) {
        val lat = lo + i * step
        val cur = eval(lat, lng, target)
        val p = prev
        if (p[0].isNaN() != cur[0].isNaN()) {
          if (depth > 0) result += scan(lng, target, prevLat, lat, EDGE_SUBDIVISIONS, depth - 1)
        } else if (!p[0].isNaN() && (p[0] < 0) != (cur[0] < 0) && (cur[0] - p[0]).absoluteValue < 180.0 &&
          // 變號、不是 ±180 的換圈、到角時刻沒有跳到另一天
          (cur[1] - p[1]).absoluteValue < 0.25) {
          var a = prevLat
          var b = lat
          var fa = p[0]
          for (round in 0 until REFINE_ROUNDS) {
            val mid = (a + b) / 2
            val fm = eval(mid, lng, target)[0]
            if (fm.isNaN()) break
            if ((fa < 0) == (fm < 0)) { a = mid; fa = fm } else b = mid
          }
          result += (a + b) / 2
        }
        prevLat = lat
        prev = cur
      }
      return result
    }

    companion object {
      fun of(ctx: AstroGeoContext, paran: Paran, basis: AstroGeoBasis): Moving? {
        if (paran.anchor !in ctx.tracks && paran.partner !in ctx.tracks) return null
        val a = ctx.equatorial[paran.anchor]?.get(basis) ?: return null
        val b = ctx.equatorial[paran.partner]?.get(basis) ?: return null
        return Moving(ctx, paran, basis, a, b)
      }
    }
  }

  companion object {

    private val MERIDIAN = setOf(GeoAngle.MC, GeoAngle.IC)

    /** 取樣點數（兩顆都在地平線、或世俗相位時）：緯度範圍內等分，約 1° 一點 */
    private const val SAMPLES = 180
    private const val BISECT_ROUNDS = 50

    /** 恆星時每日走幾度 */
    private const val SIDEREAL_DEG_PER_DAY = 360.98564736629
    /** 精算時的搜尋範圍：單點掃全部緯度、取最近靜態解的（月亮移動後原本那支可能消失、別處出現新的）；畫曲線時沿上一個經度的解往下找 */
    private const val WIDE_WINDOW = 180.0
    private const val WIDE_STEP = 1.0
    private const val NARROW_WINDOW = 3.0
    private const val NARROW_STEP = 0.25
    private const val MAX_LAT = 89.9
    /** 2^-30 × 1° ≈ 0.003"，足夠 */
    private const val REFINE_ROUNDS = 30
    private const val SECANT_DELTA = 0.05
    private const val EDGE_SUBDIVISIONS = 40
    private const val SECANT_ROUNDS = 12

    /** anchor 在 [a]、與它成 [aspect]（合衝刑）的 partner 在哪些角 */
    internal fun partnerAngles(a: GeoAngle, aspect: Aspect): List<GeoAngle> = when (aspect) {
      Aspect.CONJUNCTION -> listOf(a)
      Aspect.OPPOSITION  -> listOf(opposite(a))
      Aspect.SQUARE      -> if (a in MERIDIAN) listOf(GeoAngle.ASC, GeoAngle.DESC) else listOf(GeoAngle.MC, GeoAngle.IC)
      else               -> emptyList()
    }

    private fun opposite(a: GeoAngle) = when (a) {
      GeoAngle.MC   -> GeoAngle.IC
      GeoAngle.IC   -> GeoAngle.MC
      GeoAngle.ASC  -> GeoAngle.DESC
      GeoAngle.DESC -> GeoAngle.ASC
    }

    /** 世俗位置（MC＝0、DSC＝90、IC＝180、ASC＝270）；角上的星就是這四個值 */
    private fun mundaneOf(a: GeoAngle) = when (a) {
      GeoAngle.MC   -> 0.0
      GeoAngle.DESC -> 90.0
      GeoAngle.IC   -> 180.0
      GeoAngle.ASC  -> 270.0
    }

    /** 世俗相位的兩個方向（合、衝只有一個） */
    private fun offsets(aspect: Aspect): List<Double> =
      if (aspect.degree == 0.0 || aspect.degree == 180.0) listOf(aspect.degree) else listOf(aspect.degree, -aspect.degree)

    /** 半日弧（度），以 tan φ、tan δ 計；永不升落（|tan φ tan δ| > 1）回 NaN */
    private fun semiArc(tanPhi: Double, tanDec: Double): Double {
      val cosD = -tanPhi * tanDec
      return if (cosD > 1.0 || cosD < -1.0) Double.NaN else Math.toDegrees(acos(cosD))
    }

    /** 在角 [a] 上的時角；ASC／DSC 在該緯度不升落時回 NaN */
    private fun hourAngle(a: GeoAngle, tanPhi: Double, tanDec: Double): Double = when (a) {
      GeoAngle.MC   -> 0.0
      GeoAngle.IC   -> 180.0
      GeoAngle.ASC  -> -semiArc(tanPhi, tanDec)
      GeoAngle.DESC -> semiArc(tanPhi, tanDec)
    }

    /** (−180, 180]；比 normalizeSigned 少一次除法，求根時每一點都要算 */
    private fun signed(deg: Double): Double {
      val r = (deg + 180.0) % 360.0
      return (if (r <= 0.0) r + 360.0 else r) - 180.0
    }

    /** A 在 [a]、B 在 [b] 同時成立的緯度 */
    internal fun angularLats(posA: EquatorialPos, a: GeoAngle, posB: EquatorialPos, b: GeoAngle): List<Double> {
      val aMeridian = a in MERIDIAN
      val bMeridian = b in MERIDIAN
      return when {
        aMeridian && bMeridian -> emptyList()
        // 已知 A 的時角 → B 的時角 h = α_A + HA_A − α_B
        aMeridian -> listOfNotNull(closedForm(posA.rightAscension + hourAngle(a, 0.0, 0.0) - posB.rightAscension, b, posB.declination))
        bMeridian -> listOfNotNull(closedForm(posB.rightAscension + hourAngle(b, 0.0, 0.0) - posA.rightAscension, a, posA.declination))
        else -> {
          val maxLat = 90.0 - maxOf(posA.declination.absoluteValue, posB.declination.absoluteValue)
          val tanA = tan(Math.toRadians(posA.declination))
          val tanB = tan(Math.toRadians(posB.declination))
          val dAlpha = posA.rightAscension - posB.rightAscension
          roots(maxLat) { tanPhi -> signed(dAlpha + hourAngle(a, tanPhi, tanA) - hourAngle(b, tanPhi, tanB)) }
        }
      }
    }

    /**
     * 地平線上的那顆星時角必須是 [hourAngle]（東昇為負、西落為正）：由半日弧反解緯度。
     * 赤緯為 0 時半日弧恆為 90°，與緯度無關，不成線。
     */
    private fun closedForm(hourAngle: Double, angle: GeoAngle, dec: Double): Double? {
      val h = hourAngle.normalizeSigned()
      val arc = when (angle) {
        GeoAngle.ASC  -> if (h < 0) -h else return null
        GeoAngle.DESC -> if (h > 0) h else return null
        else          -> return null
      }
      val tanDec = tan(Math.toRadians(dec))
      if (tanDec.absoluteValue < 1e-12) return null
      return Math.toDegrees(atan(-cos(Math.toRadians(arc)) / tanDec))
    }

    /**
     * A 在 [a] 的同一刻，B 的世俗位置與 A 相距 [offset]。
     * B 必須會升落（半日弧在 0–180 之間），否則該緯度沒有世俗位置。
     */
    internal fun mundaneLats(posA: EquatorialPos, a: GeoAngle, posB: EquatorialPos, offset: Double): List<Double> {
      val maxLat = 90.0 - maxOf(posB.declination.absoluteValue, if (a in MERIDIAN) 0.0 else posA.declination.absoluteValue)
      val target = mundaneOf(a) + offset
      val tanA = tan(Math.toRadians(posA.declination))
      val tanB = tan(Math.toRadians(posB.declination))
      val dAlpha = posA.rightAscension - posB.rightAscension
      return roots(maxLat) { tanPhi ->
        val dsa = semiArc(tanPhi, tanB)
        if (dsa > 0.0 && dsa < 180.0)
          signed(mundanePosition(signed(dAlpha + hourAngle(a, tanPhi, tanA)), dsa) - target)
        else
          Double.NaN
      }
    }

    /** 時角 [ha]（−180..180，西為正）、半日弧 [dsa] 的星的世俗位置（0..360） */
    internal fun mundanePosition(ha: Double, dsa: Double): Double {
      val nsa = 180.0 - dsa
      return when {
        ha in 0.0..dsa  -> 90.0 * ha / dsa
        ha > dsa        -> 90.0 + 90.0 * (ha - dsa) / nsa
        ha < -dsa       -> 180.0 + 90.0 * (ha + 180.0) / nsa
        else            -> 270.0 + 90.0 * (ha + dsa) / dsa
      }
    }

    /**
     * (−[maxLat], [maxLat]) 內 [f] 的根：等分取樣找變號再二分。[f] 吃的是 tan φ（省掉每一點的度轉弧度與 tan），
     * 回 NaN 表示該緯度無定義。|Δf| ≥ 180 是 ±180 的換圈，不是根。
     */
    private inline fun roots(maxLat: Double, f: (Double) -> Double): List<Double> {
      val limit = minOf(maxLat, 90.0) - 1e-9
      if (limit <= 0.0) return emptyList()
      val step = 2 * limit / SAMPLES
      val result = mutableListOf<Double>()
      var prevLat = -limit
      var prev = f(tan(Math.toRadians(prevLat)))
      for (i in 1..SAMPLES) {
        val lat = -limit + i * step
        val cur = f(tan(Math.toRadians(lat)))
        val p = prev
        if (!p.isNaN() && !cur.isNaN() && (p < 0) != (cur < 0) && (cur - p).absoluteValue < 180.0) {
          var lo = prevLat
          var hi = lat
          var fLo = p
          for (round in 0 until BISECT_ROUNDS) {
            val mid = (lo + hi) / 2
            val fMid = f(tan(Math.toRadians(mid)))
            if (fMid.isNaN()) break
            if ((fLo < 0) == (fMid < 0)) {
              lo = mid
              fLo = fMid
            } else {
              hi = mid
            }
          }
          result += (lo + hi) / 2
        } else if (cur == 0.0) {
          result += lat
        }
        prevLat = lat
        prev = cur
      }
      return result
    }
  }
}
