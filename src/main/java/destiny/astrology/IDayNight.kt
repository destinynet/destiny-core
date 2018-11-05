/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:39:56
 */
package destiny.astrology

import destiny.core.DayNight
import destiny.core.Descriptive
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 區分日夜的介面
 */
interface IDayNight : Descriptive {

  fun getDayNight(gmtJulDay: Double, location: ILocation): DayNight

  fun getDayNight(lmt: ChronoLocalDateTime<*>, location: ILocation): DayNight {
    val gmt = TimeTools.getGmtFromLmt(lmt, location)
    return getDayNight(TimeTools.getGmtJulDay(gmt), location)
  }
}
