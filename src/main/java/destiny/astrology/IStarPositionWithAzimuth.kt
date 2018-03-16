/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:51:54
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime

/**
 * 計算星體位置 + 地平方位角 (限定 Star) , <BR></BR>
 * Swiss Ephemeris 實作為 StarPositionWithAzimuthImpl
 */
interface IStarPositionWithAzimuth : IStarPosition<Position> {

  override fun getPosition(star: Star, gmtJulDay: Double, geoLng: Double, geoLat: Double, geoAlt: Double?, centric: Centric, coordinate: Coordinate, temperature: Double, pressure: Double): PositionWithAzimuth

  fun getPositionFromGmt(star: Star, gmt: ChronoLocalDateTime<*>, location: Location, centric: Centric, coordinate: Coordinate, temperature: Double, pressure: Double): PositionWithAzimuth {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getPosition(star, gmtJulDay, location.longitude, location.latitude, location.altitudeMeter, centric, coordinate, temperature, pressure)
  }

  /**
   * 內定 溫度 攝氏零度，壓力 1013.25
   */
  fun getPositionFromGmt(star: Star, gmt: ChronoLocalDateTime<*>, location: Location, centric: Centric, coordinate: Coordinate): PositionWithAzimuth {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getPosition(star, gmtJulDay, location.longitude, location.latitude, location.altitudeMeter, centric, coordinate, 0.0, 1013.25)
  }


}
