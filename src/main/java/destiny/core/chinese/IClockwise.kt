/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

interface IClockwise : Descriptive {

  fun getClockwise(gmtJulDay: GmtJulDay, loc: ILocation): Clockwise

  fun getClockwise(lmt: ChronoLocalDateTime<*>, loc: ILocation): Clockwise {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getClockwise(gmtJulDay, loc)
  }
}
