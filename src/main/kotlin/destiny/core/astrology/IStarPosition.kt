/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:28:37
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

/**
 * 取得星體的位置。支援 [Planet] , [Asteroid] , [Hamburger]
 * Swiss Ephemeris 的實作是 [StarPositionImpl]
 * 原廠還支援 月亮的 Apsis , 但我使用 [IApsis] 來實作
 */
interface IStarPosition<out T : IStarPos> {

  fun getPosition(star: Star, gmtJulDay: GmtJulDay, centric: Centric, coordinate: Coordinate): T

  // ============================================================
  // New API with CalculationOptions - facilitates gradual migration
  // ============================================================

  /**
   * Calculate star position with explicit calculation options.
   *
   * The [options] parameter overrides calculation types embedded in certain Star objects:
   * - For [LunarNode]: options.nodeType overrides star.nodeType (TRUE/MEAN)
   * - For [LunarApsis]: options.apsisType overrides star.meanOscu (OSCU/MEAN)
   * - For other stars: options has no effect
   *
   * This design allows gradual migration from the old API where calculation type is embedded in Star.
   *
   * Example:
   * ```
   * // Old API - calculation type embedded in Star
   * getPosition(LunarNode.NORTH_TRUE, gmtJulDay, centric, coordinate)
   *
   * // New API - calculation type in options (overrides Star's embedded type)
   * calculate(LunarNode.NORTH_TRUE, gmtJulDay, centric, coordinate, CalculationOptions(nodeType = MEAN))
   * // Result: uses MEAN calculation despite NORTH_TRUE being passed
   * ```
   */
  fun calculate(star: Star, gmtJulDay: GmtJulDay, centric: Centric, coordinate: Coordinate, options: StarTypeOptions = StarTypeOptions.DEFAULT): T

  /**
   * Calculate positions for multiple stars with explicit calculation options.
   *
   * @see calculateWithAzimuth for details on how options override Star's embedded calculation types
   */
  fun calculateAll(stars: Iterable<Star>, gmtJulDay: GmtJulDay, centric: Centric, coordinate: Coordinate, options: StarTypeOptions = StarTypeOptions.DEFAULT): Map<Star, T>

  /** 同樣是求 Position , 但多傳入地點、溫度、壓力 等資料 , 在此直接 discard 掉  */
  fun calculateWithAzimuth(star: Star,
                           gmtJulDay: GmtJulDay,
                           loc: ILocation,
                           centric: Centric = Centric.GEO,
                           coordinate: Coordinate = Coordinate.ECLIPTIC,
                           temperature: Double = 0.0,
                           pressure: Double = 1013.25,
                           options: StarTypeOptions = StarTypeOptions.DEFAULT): T {
    return calculate(star, gmtJulDay, centric, coordinate, options)
  }

  fun calculate(star: Star, gmtJulDay: GmtJulDay, loc: ILocation, centric: Centric = Centric.GEO, coordinate: Coordinate = Coordinate.ECLIPTIC, options: StarTypeOptions = StarTypeOptions.DEFAULT): T {
    return calculate(star, gmtJulDay, centric, coordinate, options)
  }
}
