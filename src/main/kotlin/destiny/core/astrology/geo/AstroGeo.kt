/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Astronomical
import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.EquatorialPos
import destiny.core.astrology.IStarPos
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.Star
import destiny.core.astrology.StarTypeOptions
import destiny.core.astrology.toEquatorial
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILatLng
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.LatLng
import destiny.core.calendar.Lng.Companion.toLng
import destiny.tools.CircleTools.normalize
import destiny.tools.CircleTools.normalizeSigned
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/** 幾何基準：星體本體（含黃緯→赤緯）vs 黃道度數（β=0 投影） */
enum class AstroGeoBasis { IN_MUNDO, ZODIACAL }

/** 四個角 */
enum class GeoAngle { MC, IC, ASC, DESC }

/**
 * 某一時刻的天球↔地表對應脈絡。與地點無關 —— 一個時刻只算一次，可 cache。
 *
 * @param gmstDeg θ = Astronomical.gmst(jd) * 15，單位度
 */
data class AstroGeoContext(
  val gmtJulDay: GmtJulDay,
  val gmstDeg: Double,
  val equatorial: Map<AstroPoint, Map<AstroGeoBasis, EquatorialPos>>,
) {
  companion object {

    /**
     * 由星曆計算器建構 context：一次算出各星兩個 basis 的 (α, δ)。
     * IN_MUNDO 直接取赤道座標；ZODIACAL 取黃經後以 β=0 投影。
     * [obliquityDeg] 由呼叫端的 [destiny.core.astrology.IObliquityCalculator] 提供；
     * 算不出座標的星直接略過。
     */
    fun of(
      gmtJulDay: GmtJulDay,
      stars: Collection<Star>,
      starPosition: IStarPosition<IStarPos>,
      obliquityDeg: Double,
      centric: Centric = Centric.GEO,
      starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN,
    ): AstroGeoContext = ofCyclo(gmtJulDay, gmtJulDay, stars, starPosition, obliquityDeg, centric, starTypeOptions)

    /**
     * CCG 混血 context（設計 doc 5.4）：天空凍結在本命恆星時（θ 取 [natalGmtJulDay]），
     * 星體位置取行運時刻 [transitGmtJulDay] —— 語意為「行運星壓上 relocated 本命四角」。
     * 回傳 context 的 [AstroGeoContext.gmtJulDay] 為行運時刻（位置的時刻）。
     */
    fun ofCyclo(
      natalGmtJulDay: GmtJulDay,
      transitGmtJulDay: GmtJulDay,
      stars: Collection<Star>,
      starPosition: IStarPosition<IStarPos>,
      obliquityDeg: Double,
      centric: Centric = Centric.GEO,
      starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN,
    ): AstroGeoContext {
      val gmstDeg = (Astronomical.gmst(natalGmtJulDay) * 15.0).normalize()
      val map = buildMap<AstroPoint, Map<AstroGeoBasis, EquatorialPos>> {
        stars.forEach { star ->
          val inMundo = star.toEquatorial(transitGmtJulDay, centric, starPosition, starTypeOptions) ?: return@forEach
          val eclLng = runCatching {
            starPosition.calculate(star, transitGmtJulDay, centric, Coordinate.ECLIPTIC, starTypeOptions).lng
          }.getOrNull() ?: return@forEach
          put(star, mapOf(
            AstroGeoBasis.IN_MUNDO to inMundo,
            AstroGeoBasis.ZODIACAL to Astronomical.eclipticToEquatorial(eclLng, 0.0, obliquityDeg)
          ))
        }
      }
      return AstroGeoContext(transitGmtJulDay, gmstDeg, map)
    }
  }
}

internal const val EARTH_RADIUS_KM = 6371.0

/**
 * 反解：某緯度 [latDeg] 下，星體 [p] 位於角 [angle] 的地理經度，東經為正，normalized to (-180, 180]。
 *
 * MC/IC 與緯度無關（垂直半子午線）；AS/DS 的存在條件為 |tan φ · tan δ| ≤ 1
 * （即 |φ| ≤ 90 − |δ|），不滿足時回 null —— 這就是 AS/DS 曲線在高緯度斷掉的原因。
 * 星體不在 context 內也回 null。
 */
fun AstroGeoContext.longitudeAt(p: AstroPoint, angle: GeoAngle, latDeg: Double, basis: AstroGeoBasis): Double? {
  val pos = equatorial[p]?.get(basis) ?: return null
  return longitudeAt(pos, angle, latDeg)
}

private fun AstroGeoContext.longitudeAt(pos: EquatorialPos, angle: GeoAngle, latDeg: Double): Double? {
  val alpha = pos.rightAscension
  return when (angle) {
    GeoAngle.MC -> (alpha - gmstDeg).normalizeSigned()
    GeoAngle.IC -> (alpha - gmstDeg + 180.0).normalizeSigned()

    GeoAngle.ASC, GeoAngle.DESC -> {
      if (latDeg.absoluteValue >= 90.0)
        return null
      // 星體在地平線上：cos H = −tan φ · tan δ , H ∈ [0°, 180°]
      val cosH = -tan(Math.toRadians(latDeg)) * tan(Math.toRadians(pos.declination))
      if (cosH.absoluteValue > 1.0)
        return null
      val h = Math.toDegrees(acos(cosH))
      // 東昇 HA = −H , 西落 HA = +H ; λ = α + HA − θ
      val lng = if (angle == GeoAngle.ASC) alpha - h - gmstDeg else alpha + h - gmstDeg
      lng.normalizeSigned()
    }
  }
}

/**
 * 地點 [loc] 到星體 [p] 的 [angle] 線的地表大圓角距（度）—— angularity 的統一度量（設計 doc 4.4）。
 * 星體不在 context 內回 null。
 */
fun AstroGeoContext.angularityToLine(loc: ILatLng, p: AstroPoint, angle: GeoAngle, basis: AstroGeoBasis): Double? {
  val pos = equatorial[p]?.get(basis) ?: return null
  return when (angle) {
    GeoAngle.MC, GeoAngle.IC -> {
      val lineLng = longitudeAt(pos, angle, 0.0)!!
      meridianDistanceDeg(loc.lat.value, loc.lng.value, lineLng)
    }

    // 無閉合解：取樣 + 三分法局部細化
    GeoAngle.ASC, GeoAngle.DESC -> ascDescDistanceDeg(loc, pos, angle)
  }
}

private fun AstroGeoContext.ascDescDistanceDeg(loc: ILatLng, pos: EquatorialPos, angle: GeoAngle): Double? {
  val maxLat = minOf(90.0 - pos.declination.absoluteValue - 1e-6, 89.9)
  if (maxLat <= 0.0)
    return null   // |δ| ≈ 90 , 曲線不存在

  fun distAt(lat: Double): Double =
    longitudeAt(pos, angle, lat)
      ?.let { gcDistanceDeg(loc.lat.value, loc.lng.value, lat, it) }
      ?: Double.MAX_VALUE

  val step = 0.5
  var bestLat = Double.NaN
  var bestDist = Double.MAX_VALUE
  var lat = -maxLat
  while (lat <= maxLat + 1e-9) {
    val d = distAt(lat)
    if (d < bestDist) {
      bestDist = d
      bestLat = lat
    }
    lat += step
  }
  if (bestLat.isNaN())
    return null

  var lo = maxOf(bestLat - step, -maxLat)
  var hi = minOf(bestLat + step, maxLat)
  repeat(80) {
    val m1 = lo + (hi - lo) / 3
    val m2 = hi - (hi - lo) / 3
    if (distAt(m1) <= distAt(m2)) hi = m2 else lo = m1
  }
  return distAt((lo + hi) / 2).coerceAtMost(bestDist)
}

/** 兩點間大圓角距（度），haversine */
private fun gcDistanceDeg(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
  val phi1 = Math.toRadians(lat1)
  val phi2 = Math.toRadians(lat2)
  val a = sin(Math.toRadians(lat2 - lat1) / 2).pow(2) +
    cos(phi1) * cos(phi2) * sin(Math.toRadians(lng2 - lng1) / 2).pow(2)
  return Math.toDegrees(2 * asin(sqrt(a).coerceAtMost(1.0)))
}

/** [angularityToLine] 的公里版：deg × (π/180) × 6371 */
fun AstroGeoContext.kmToLine(loc: ILatLng, p: AstroPoint, angle: GeoAngle, basis: AstroGeoBasis): Double? =
  angularityToLine(loc, p, angle, basis)?.let { Math.toRadians(it) * EARTH_RADIUS_KM }

/** 正解：給地點，每顆星到各角線的地表大圓角距（度） */
fun AstroGeoContext.angularity(loc: ILatLng, basis: AstroGeoBasis): Map<AstroPoint, Map<GeoAngle, Double>> =
  equatorial.keys.associateWith { p ->
    GeoAngle.entries.mapNotNull { angle ->
      angularityToLine(loc, p, angle, basis)?.let { angle to it }
    }.toMap()
  }.filterValues { it.isNotEmpty() }

/**
 * 取樣成可畫折線：處理 ±180 換日線切段與極區截斷。
 * MC/IC 為單一垂直線段；AS/DS 依 [latStep] 取樣，遇換日線或極區截斷即切段。
 */
fun AstroGeoContext.line(p: AstroPoint, angle: GeoAngle, basis: AstroGeoBasis, latStep: Double = 1.0): List<List<ILatLng>> {
  require(latStep > 0) { "latStep must be positive" }
  val pos = equatorial[p]?.get(basis) ?: return emptyList()
  val maxLat = 89.0
  val segments = mutableListOf<MutableList<ILatLng>>()
  var current: MutableList<ILatLng>? = null
  var prevLng: Double? = null
  var lat = -maxLat
  while (lat <= maxLat + 1e-9) {
    val lng = longitudeAt(pos, angle, lat)
    if (lng == null) {
      current = null
      prevLng = null
    } else {
      val wrap = prevLng?.let { (lng - it).absoluteValue > 180.0 } == true
      if (current == null || wrap) {
        current = mutableListOf<ILatLng>().also { segments.add(it) }
      }
      current.add(LatLng(lat.toLat(), lng.toLng()))
      prevLng = lng
    }
    lat += latStep
  }
  return segments
}

/**
 * Local Space：從 [loc] 看各星體的方位角（度，N=0 順時針，大圓航向）。
 * 方位是從所在地畫的，任何距離都有方向，不受經度門檻限制。
 */
fun AstroGeoContext.localSpace(loc: ILatLng, basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO): Map<AstroPoint, Double> =
  equatorial.mapNotNull { (p, m) ->
    m[basis]?.let { pos ->
      val h = Math.toRadians((gmstDeg + loc.lng.value - pos.rightAscension).normalizeSigned())
      val phi = Math.toRadians(loc.lat.value)
      val delta = Math.toRadians(pos.declination)
      val az = Math.toDegrees(
        atan2(-cos(delta) * sin(h), cos(phi) * sin(delta) - sin(phi) * cos(delta) * cos(h))
      ).normalize()
      p to az
    }
  }.toMap()

/**
 * paran：兩星同時角宮化的地點（兩條角線的交點），對緯度做一維求根。
 * MC/IC 線是垂直平行線，兩條都是 MC/IC 時永不相交 → 直接回空。
 */
fun AstroGeoContext.parans(p1: AstroPoint, a1: GeoAngle, p2: AstroPoint, a2: GeoAngle, basis: AstroGeoBasis): List<ILatLng> {
  val meridian = setOf(GeoAngle.MC, GeoAngle.IC)
  if (a1 in meridian && a2 in meridian)
    return emptyList()
  val pos1 = equatorial[p1]?.get(basis) ?: return emptyList()
  val pos2 = equatorial[p2]?.get(basis) ?: return emptyList()

  fun diff(lat: Double): Double? {
    val l1 = longitudeAt(pos1, a1, lat) ?: return null
    val l2 = longitudeAt(pos2, a2, lat) ?: return null
    return (l1 - l2).normalizeSigned()
  }

  val step = 0.25
  val maxLat = 89.75
  val result = mutableListOf<ILatLng>()
  var prevLat = -maxLat
  var prevF = diff(prevLat)
  var lat = -maxLat + step
  while (lat <= maxLat + 1e-9) {
    val f = diff(lat)
    val pf = prevF
    if (pf != null && f != null) {
      val signChange = (pf < 0 && f >= 0) || (pf > 0 && f <= 0)
      // |Δf| ≥ 180 是 normalizeSigned 在 ±180 的 wrap（兩線相距半圈），不是交點
      if (signChange && (f - pf).absoluteValue < 180.0) {
        var lo = prevLat
        var hi = lat
        var fLo: Double = pf
        repeat(60) {
          val mid = (lo + hi) / 2
          val fMid = diff(mid) ?: return@repeat
          if ((fLo < 0) == (fMid < 0)) {
            lo = mid
            fLo = fMid
          } else {
            hi = mid
          }
        }
        val rootLat = (lo + hi) / 2
        longitudeAt(pos1, a1, rootLat)?.also { result.add(LatLng(rootLat.toLat(), it.toLng())) }
      }
    }
    prevLat = lat
    prevF = f
    lat += step
  }
  return result
}

/**
 * 點到「半」子午線（MC 線或 IC 線）的大圓角距（度），分段閉合解。
 *
 * |Δλ| ≤ 90°：垂足落在 [lineLngDeg] 這半邊，d = asin(|cos φ · sin Δλ|)。
 * |Δλ| > 90°：垂足落在對面半邊（那是同星的另一條角線），半子午線的最近點是極點，
 * d = 90° − |φ|。不分段會把 IC 線誤報為 MC 線（整個對面半球受影響）。
 */
private fun meridianDistanceDeg(latDeg: Double, lngDeg: Double, lineLngDeg: Double): Double {
  val dLng = (lngDeg - lineLngDeg).normalizeSigned()
  return if (dLng.absoluteValue <= 90.0) {
    Math.toDegrees(asin((cos(Math.toRadians(latDeg)) * sin(Math.toRadians(dLng))).absoluteValue))
  } else {
    90.0 - latDeg.absoluteValue
  }
}
