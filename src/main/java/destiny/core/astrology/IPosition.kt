/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

interface IPosition<out T : Point> {

  val point: T

  fun getPosition(gmtJulDay: GmtJulDay,
                  loc: ILocation,
                  centric: Centric = Centric.GEO,
                  coordinate: Coordinate = Coordinate.ECLIPTIC,
                  temperature: Double = 0.0,
                  pressure: Double = 1013.25): IPos


  fun getPosition(lmt: ChronoLocalDateTime<*>,
                  loc: ILocation,
                  centric: Centric = Centric.GEO,
                  coordinate: Coordinate = Coordinate.ECLIPTIC): IPos {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getPosition(gmtJulDay, loc, centric, coordinate, 0.0, 1013.25)
  }


}
