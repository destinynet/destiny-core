/**
 * @author smallufo
 * Created on 2007/5/28 at 上午 4:22:14
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation

/**
 * 計算星球南北交點
 * Swiss Ephemeris 實作是 ApsisWithAzimuthImpl
 */
interface IApsisWithAzimuth : IApsis {

  fun getPositionsWithAzimuths(star: Star, gmtJulDay: GmtJulDay, coordinate: Coordinate, nodeType: NodeType, loc: ILocation, temperature: Double = 0.0, pressure: Double = 1013.25): Map<Apsis, StarPosWithAzimuth>

  fun getPositionWithAzimuth(star: Star, apsis: Apsis, gmtJulDay: GmtJulDay, coordinate: Coordinate, nodeType: NodeType, loc: ILocation, temperature: Double = 0.0, pressure: Double = 1013.25): StarPosWithAzimuth {
    val map = getPositionsWithAzimuths(star, gmtJulDay, coordinate, nodeType, loc, temperature, pressure)
    return map[apsis] ?: throw RuntimeException("Cannot found StarPositionWithAzimuth of the Apsis : $apsis")
  }

}
