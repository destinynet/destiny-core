/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools.toGmtJulDay
import java.time.chrono.ChronoLocalDateTime

interface IPosition<out T : AstroPoint> {

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
    return getPosition(lmt.toGmtJulDay(loc), loc, centric, coordinate, 0.0, 1013.25)
  }


}
