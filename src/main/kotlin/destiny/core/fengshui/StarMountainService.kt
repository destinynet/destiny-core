/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.fengshui

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import jakarta.inject.Named

/**
 * 星體 × 山：把「純天文」的方位角計算 ([IAzimuthTransit] / [IStarPositionWithAzimuthCalculator])
 * 對應到「風水」的 24 山（三盤 [AbstractMountainCompass]）。
 *
 * 純演算法組合，不含 swisseph，故置於 destiny-core。
 */
@Named
class StarMountainService(
  private val azimuthTransit: IAzimuthTransit,
  private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
) {

  /** 此時此地，[star] 的地平方位角落在哪座山 */
  fun getMountainOf(
    star: Star, gmt: GmtJulDay, loc: ILocation,
    compass: AbstractMountainCompass = EarthlyCompass(),
    centric: Centric = Centric.GEO,
    temperature: Double = 0.0, pressure: Double = 1013.25,
    options: StarTypeOptions = StarTypeOptions.MEAN,
  ): Mountain {
    val az = azimuthOf(star, gmt, loc, centric, temperature, pressure, options)
    return compass.get(az)
  }

  /** 一組星體各自落在哪座山 */
  fun getMountainsOf(
    stars: Collection<Star>, gmt: GmtJulDay, loc: ILocation,
    compass: AbstractMountainCompass = EarthlyCompass(),
    centric: Centric = Centric.GEO,
    temperature: Double = 0.0, pressure: Double = 1013.25,
    options: StarTypeOptions = StarTypeOptions.MEAN,
  ): Map<Star, Mountain> {
    return stars.associateWith { getMountainOf(it, gmt, loc, compass, centric, temperature, pressure, options) }
  }

  /**
   * [star]（通常日/月）從 [fromGmt] 起，下一次「進入並離開」[mountain] 的時間窗。
   * 方向無關（南北半球、逆行折返皆適用）：
   * - **進入** = 星體方位角穿越任一邊界後「落入」此山（遞增時經 startDeg、遞減時經 endDeg）。
   * - **離開** = 進入後下一次穿越邊界並「離開」此山（正常經對側；逆行折返時經原邊界，見 [MountainTransit.isRetreat]）。
   *
   * 若掃描窗內星體到不了此山（如 δ>φ 的太陽擺盪於正北、永遠進不了正南的午山）則回 null。
   */
  fun getNextMountainTransit(
    star: Star, mountain: Mountain, fromGmt: GmtJulDay, loc: ILocation,
    compass: AbstractMountainCompass = EarthlyCompass(),
    centric: Centric = Centric.GEO,
    temperature: Double = 0.0, pressure: Double = 1013.25,
    options: StarTypeOptions = StarTypeOptions.MEAN,
  ): MountainTransit? {
    val startDeg = compass.getStartDegree(mountain)
    val endDeg = compass.getEndDegree(mountain)

    // 穿越後 (真實時間往後探測) 是否落在此山內
    fun insideAfter(t: GmtJulDay): Boolean =
      compass.get(azimuthOf(star, t + PROBE, loc, centric, temperature, pressure, options)) == mountain

    // 兩邊界之中，時間上最先發生的穿越
    fun nextBoundary(from: GmtJulDay): AzimuthTransit? {
      val s = azimuthTransit.getNextAzimuthTransit(star, startDeg, from, loc, true, centric, false, temperature, pressure, options)
      val e = azimuthTransit.getNextAzimuthTransit(star, endDeg, from, loc, true, centric, false, temperature, pressure, options)
      return listOfNotNull(s, e).minByOrNull { it.gmtJulDay.value }
    }

    // 進入：往後第一個「穿越後落在此山內」的邊界穿越
    var enter: AzimuthTransit? = null
    var t = fromGmt
    var guard = 0
    while (enter == null && guard++ < MAX_BOUNDARY_HOPS) {
      val c = nextBoundary(t) ?: return null
      if (insideAfter(c.gmtJulDay)) enter = c
      else t = c.gmtJulDay + PROBE
    }
    if (enter == null) return null

    // 離開：進入後第一個「穿越後不在此山」的邊界穿越
    t = enter.gmtJulDay + PROBE
    guard = 0
    while (guard++ < MAX_BOUNDARY_HOPS) {
      val c = nextBoundary(t) ?: return null
      if (!insideAfter(c.gmtJulDay)) return MountainTransit(mountain, enter, c)
      t = c.gmtJulDay + PROBE
    }
    return null
  }

  private fun azimuthOf(
    star: Star, gmt: GmtJulDay, loc: ILocation,
    centric: Centric, temperature: Double, pressure: Double, options: StarTypeOptions,
  ): Double =
    starPosWithAzimuth.getPositionWithAzimuth(star, gmt, loc, centric, Coordinate.ECLIPTIC, temperature, pressure, options).azimuthDeg

  companion object {
    /** 判定「進入方向」用的時間探測間隔 (天)：約 60 秒。須小到不會越過整座 15° 山 */
    private const val PROBE = 60.0 / 86400.0

    /** 進入/離開搜尋的邊界穿越次數上限 (防呆，正常 1~3 次即可定位) */
    private const val MAX_BOUNDARY_HOPS = 12
  }
}
