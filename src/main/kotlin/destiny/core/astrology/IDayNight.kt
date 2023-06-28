/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:39:56
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.Descriptive
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 區分日夜的介面
 */
interface IDayNight : Descriptive {

  fun getDayNight(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig = TransConfig(discCenter = false, refraction = true)): DayNight

  fun getDayNight(lmt: ChronoLocalDateTime<*>, loc: ILocation, transConfig: TransConfig = TransConfig(discCenter = false, refraction = true)): DayNight {
    val gmt = TimeTools.getGmtFromLmt(lmt, loc)
    return getDayNight(TimeTools.getGmtJulDay(gmt), loc, transConfig)
  }
}
