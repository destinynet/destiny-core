/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:51:54
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算星體位置 + 地平方位角 (限定 Star) , <BR></BR>
 * Swiss Ephemeris 實作為 StarPositionWithAzimuthImpl
 */
interface IStarPositionWithAzimuthCalculator : IStarPosition<IStarPos> {

//  override fun getPosition(star: Star,
//                           gmtJulDay: Double,
//                           geoLat: Double,
//                           geoLng: Double,
//                           geoAlt: Double?,
//                           centric: Centric,
//                           coordinate: Coordinate,
//                           temperature: Double,
//                           pressure: Double): IStarPositionWithAzimuth

  fun getPositionFromGmt(star: Star,
                         gmt: ChronoLocalDateTime<*>,
                         loc: ILocation,
                         centric: Centric,
                         coordinate: Coordinate,
                         temperature: Double = 0.0,
                         pressure: Double = 1013.25): IStarPositionWithAzimuth
//  {
//    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
//    return getPosition(star, gmtJulDay, loc.lat, loc.lng, loc.altitudeMeter, centric, coordinate,
//      temperature, pressure)
//  }


}
