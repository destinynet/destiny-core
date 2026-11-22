/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.tools.toRadians
import destiny.tools.toDegrees
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
import destiny.tools.ExtremumTools
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
  /**
   * 一天之內位置會明顯移動的星（月亮一天約 13°）在 [gmtJulDay] 前後的軌跡。
   * 只有 paran 用得到（[IParanCalculator.refine]：以「在某條經線上真的到角那一刻」的位置重算）；角線、計分一律用 [equatorial]。
   */
  val tracks: Map<AstroPoint, EquatorialTrack> = emptyMap(),
) {

  /**
   * 帶上 [points] 的軌跡（[gmtJulDay] 前後各 [EquatorialTrack.HALF_SPAN_DAYS] 天、逐時取樣）。
   * 傳入的 [obliquityDeg] 用於黃道投影（整段視為不變）。
   */
  fun withTracks(
    points: Collection<Star>,
    starPosition: IStarPosition<IStarPos>,
    obliquityDeg: Double,
    centric: Centric = Centric.GEO,
    starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN,
  ): AstroGeoContext {
    val n = (2 * EquatorialTrack.HALF_SPAN_DAYS / EquatorialTrack.STEP_DAYS).toInt()
    val start = gmtJulDay.value - EquatorialTrack.HALF_SPAN_DAYS
    val built = points.mapNotNull { star ->
      val samples = (0..n).map { i -> positionsOf(star, GmtJulDay(start + i * EquatorialTrack.STEP_DAYS), starPosition, obliquityDeg, centric, starTypeOptions) ?: return@mapNotNull null }
      star to EquatorialTrack(start, EquatorialTrack.STEP_DAYS, AstroGeoBasis.entries.associateWith { b -> samples.map { it.getValue(b) } })
    }.toMap()
    return copy(tracks = tracks + built)
  }

  companion object {

    /** 一顆星在 [jd] 的兩種基準座標；算不出回 null */
    private fun positionsOf(
      star: Star, jd: GmtJulDay, starPosition: IStarPosition<IStarPos>, obliquityDeg: Double, centric: Centric, starTypeOptions: StarTypeOptions,
    ): Map<AstroGeoBasis, EquatorialPos>? {
      val inMundo = star.toEquatorial(jd, centric, starPosition, starTypeOptions) ?: return null
      val eclLng = runCatching { starPosition.calculate(star, jd, centric, Coordinate.ECLIPTIC, starTypeOptions).lng }.getOrNull() ?: return null
      return mapOf(
        AstroGeoBasis.IN_MUNDO to inMundo,
        AstroGeoBasis.ZODIACAL to Astronomical.eclipticToEquatorial(eclLng, 0.0, obliquityDeg),
      )
    }

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
        stars.forEach { star -> positionsOf(star, transitGmtJulDay, starPosition, obliquityDeg, centric, starTypeOptions)?.let { put(star, it) } }
      }
      return AstroGeoContext(transitGmtJulDay, gmstDeg, map)
    }
  }
}

/**
 * 一顆星在一段時間內的赤道座標（等距取樣、線性內插），兩種基準各一份。
 * 月亮一小時約走 0.5°，逐時取樣的線性內插誤差約 1"（遠小於 paran 的容許度）。
 *
 * @param startJd 第一個樣本的 GMT 儒略日
 * @param stepDays 取樣間距（日）
 */
data class EquatorialTrack(
  val startJd: Double,
  val stepDays: Double,
  val samples: Map<AstroGeoBasis, List<EquatorialPos>>,
) {
  /** [jd] 的位置；超出取樣範圍時以兩端外插 */
  fun at(jd: Double, basis: AstroGeoBasis): EquatorialPos? {
    val list = samples[basis] ?: return null
    if (list.size < 2) return list.firstOrNull()
    val x = (jd - startJd) / stepDays
    val i = x.toInt().coerceIn(0, list.size - 2)
    val f = x - i
    val a = list[i]
    val b = list[i + 1]
    return EquatorialPos(
      (a.rightAscension + (b.rightAscension - a.rightAscension).normalizeSigned() * f).normalize(),
      a.declination + (b.declination - a.declination) * f,
    )
  }

  companion object {
    /** 出生前後各多久：paran 的到角時刻離出生最多半個恆星日，多取一點餘裕 */
    const val HALF_SPAN_DAYS = 0.6
    const val STEP_DAYS = 1.0 / 24
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
      val cosH = -tan(toRadians(latDeg)) * tan(toRadians(pos.declination))
      if (cosH.absoluteValue > 1.0)
        return null
      val h = toDegrees(acos(cosH))
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

  val (_, refined) = ExtremumTools.minimize(maxOf(bestLat - step, -maxLat), minOf(bestLat + step, maxLat), 1e-9, ::distAt)
  return refined.coerceAtMost(bestDist)
}

/** 兩點間大圓角距（度），haversine */
private fun gcDistanceDeg(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
  val phi1 = toRadians(lat1)
  val phi2 = toRadians(lat2)
  val a = sin(toRadians(lat2 - lat1) / 2).pow(2) +
    cos(phi1) * cos(phi2) * sin(toRadians(lng2 - lng1) / 2).pow(2)
  return toDegrees(2 * asin(sqrt(a).coerceAtMost(1.0)))
}

/** [angularityToLine] 的公里版：deg × (π/180) × 6371 */
fun AstroGeoContext.kmToLine(loc: ILatLng, p: AstroPoint, angle: GeoAngle, basis: AstroGeoBasis): Double? =
  angularityToLine(loc, p, angle, basis)?.let { toRadians(it) * EARTH_RADIUS_KM }

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
      val h = toRadians((gmstDeg + loc.lng.value - pos.rightAscension).normalizeSigned())
      val phi = toRadians(loc.lat.value)
      val delta = toRadians(pos.declination)
      val az = toDegrees(
        atan2(-cos(delta) * sin(h), cos(phi) * sin(delta) - sin(phi) * cos(delta) * cos(h))
      ).normalize()
      p to az
    }
  }.toMap()

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
    toDegrees(asin((cos(toRadians(latDeg)) * sin(toRadians(dLng))).absoluteValue))
  } else {
    90.0 - latDeg.absoluteValue
  }
}

/**
 * Zenith：星體 [p] 正在頭頂（地平高度 90°）的地點。lat = δ，lng = 該星 MC 線的經度。
 * 星體不在 context 內回 null。
 */
fun AstroGeoContext.zenith(p: AstroPoint, basis: AstroGeoBasis): ILatLng? {
  val pos = equatorial[p]?.get(basis) ?: return null
  return LatLng(pos.declination.toLat(), longitudeAt(pos, GeoAngle.MC, 0.0)!!.toLng())
}

/**
 * 從 [loc] 看星體 [p] 的地平高度（度，幾何高度，不含大氣折射與視差）。
 * sin h = sin φ · sin δ + cos φ · cos δ · cos H，H = θ + λ − α。星體不在 context 內回 null。
 */
fun AstroGeoContext.altitude(loc: ILatLng, p: AstroPoint, basis: AstroGeoBasis): Double? {
  val pos = equatorial[p]?.get(basis) ?: return null
  val phi = toRadians(loc.lat.value)
  val delta = toRadians(pos.declination)
  val h = toRadians(gmstDeg + loc.lng.value - pos.rightAscension)
  val sinAlt = sin(phi) * sin(delta) + cos(phi) * cos(delta) * cos(h)
  return toDegrees(asin(sinAlt.coerceIn(-1.0, 1.0)))
}

/**
 * 晨昏線（幾何的，太陽中心高度 = 0，不含折射）。
 *
 * [curve] 由西到東（-180 → 180）每個經度一點：該經線上太陽高度為 0 的緯度。
 * 晨昏線是大圓，每條經線恰好穿過一次，所以夜半球 ＝ 每個經度上「[curve] 到 [nightPole]」之間。
 * [nightPole] 為 -90（太陽赤緯 ≥ 0，北半球夏季，南極在夜裡）或 +90。
 */
data class Terminator(
  val subsolar: ILatLng,
  val curve: List<ILatLng>,
  val nightPole: Double,
)

/**
 * 由 context 內的太陽算晨昏線：tan φ = −cos H / tan δ。
 * 赤緯 ≈ 0（春秋分）時曲線趨近兩條垂直經線，以極小赤緯代入避免除以 0。
 * context 內沒有 [sun] 回 null。
 */
fun AstroGeoContext.terminator(lngStep: Double = 1.0, sun: AstroPoint = destiny.core.astrology.Planet.SUN): Terminator? {
  require(lngStep > 0) { "lngStep must be positive" }
  val pos = equatorial[sun]?.get(AstroGeoBasis.IN_MUNDO) ?: return null
  val dec = if (pos.declination.absoluteValue < 1e-6) 1e-6 else pos.declination
  val tanDelta = tan(toRadians(dec))
  val curve = buildList {
    var lng = -180.0
    while (lng <= 180.0 + 1e-9) {
      val h = toRadians(gmstDeg + lng - pos.rightAscension)
      val lat = toDegrees(kotlin.math.atan(-cos(h) / tanDelta))
      add(LatLng(lat.toLat(), lng.coerceAtMost(180.0).toLng()))
      lng += lngStep
    }
  }
  val subsolar = LatLng(pos.declination.toLat(), longitudeAt(pos, GeoAngle.MC, 0.0)!!.toLng())
  return Terminator(subsolar, curve, if (dec > 0) -90.0 else 90.0)
}
