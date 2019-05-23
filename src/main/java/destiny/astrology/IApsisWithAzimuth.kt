/**
 * @author smallufo
 * Created on 2007/5/28 at 上午 4:22:14
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime

/**
 * 計算星球南北交點
 * Swiss Ephemeris 實作是 ApsisWithAzimuthImpl
 */
interface IApsisWithAzimuth : IApsis {

  fun getPositionsWithAzimuths(star: Star, gmtJulDay: Double, coordinate: Coordinate, nodeType: NodeType, location: Location, temperature: Double = 0.0, pressure: Double = 1013.25): Map<Apsis, StarPosWithAzimuth>

  fun getPositionWithAzimuth(star: Star, apsis: Apsis, gmtJulDay: Double, coordinate: Coordinate, nodeType: NodeType, location: Location, temperature: Double = 0.0, pressure: Double = 1013.25): StarPosWithAzimuth

  fun getPositionWithAzimuth(star: Star, apsis: Apsis, gmt: ChronoLocalDateTime<*>, coordinate: Coordinate, nodeType: NodeType, location: Location, temperature: Double = 0.0, pressure: Double = 1013.25): StarPosWithAzimuth {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getPositionWithAzimuth(star, apsis, gmtJulDay, coordinate, nodeType, location, temperature, pressure)
  }
}
