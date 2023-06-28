/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:51:54
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算星體位置 + 地平方位角 (限定 Star) ,
 * Swiss Ephemeris 實作為 StarPositionWithAzimuthImpl
 */
interface IStarPositionWithAzimuthCalculator : IStarPosition<IStarPos> {


  fun getPositionFromGmt(star: Star,
                         gmt: ChronoLocalDateTime<*>,
                         loc: ILocation,
                         centric: Centric,
                         coordinate: Coordinate,
                         temperature: Double = 0.0,
                         pressure: Double = 1013.25): IStarPositionWithAzimuth
}
