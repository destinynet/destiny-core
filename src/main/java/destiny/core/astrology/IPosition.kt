/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

interface IPosition<out T : Point> {

  val point: T

  fun getPosition(gmtJulDay: Double,
                  loc: ILocation,
                  centric: Centric,
                  coordinate: Coordinate,
                  temperature: Double = 0.0,
                  pressure: Double = 1013.25): IPos


  fun getPosition(lmt: ChronoLocalDateTime<*>,
                  loc: ILocation,
                  centric: Centric,
                  coordinate: Coordinate): IPos {
    val gmtJulDay: Double = TimeTools.getGmtJulDay(lmt, loc)
    return getPosition(gmtJulDay, loc, centric, coordinate, 0.0, 1013.25)
  }


}
