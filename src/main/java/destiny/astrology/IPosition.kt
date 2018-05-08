/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

interface IPosition<out T : Point> {

  val point: T

  fun getPosition(gmtJulDay: Double,
                  loc: ILocation,
                  centric: Centric,
                  coordinate: Coordinate,
                  starPositionImpl: IStarPosition<*>): IPos


  fun getPosition(lmt: ChronoLocalDateTime<*>,
                  loc: ILocation,
                  centric: Centric,
                  coordinate: Coordinate,
                  starPositionImpl: IStarPosition<*>): IPos {
    val gmtJulDay: Double = TimeTools.getGmtJulDay(lmt, loc)
    return getPosition(gmtJulDay, loc, centric, coordinate, starPositionImpl)
  }


}
