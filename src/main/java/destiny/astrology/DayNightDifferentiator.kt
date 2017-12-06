/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:39:56
 */
package destiny.astrology

import destiny.core.Descriptive
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime

/**
 * 區分日夜的介面 , 內定實作是 DayNightDifferentiatorImpl
 */
interface DayNightDifferentiator : Descriptive {

  fun getDayNight(gmtJulDay: Double, location: Location): DayNight

  fun getDayNight(lmt: ChronoLocalDateTime<*>, location: Location): DayNight {
    val gmt = TimeTools.getGmtFromLmt(lmt, location)
    return getDayNight(TimeTools.getGmtJulDay(gmt), location)
  }
}
