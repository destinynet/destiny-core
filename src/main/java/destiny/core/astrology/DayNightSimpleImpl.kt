/**
 * Created by smallufo on 2017-12-24.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import java.io.Serializable
import java.time.temporal.ChronoField

class DayNightSimpleImpl(val julDayResolver: JulDayResolver) : IDayNight, Serializable {

  override fun getDayNight(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): DayNight {

    val lmt = TimeTools.getLmtFromGmt(julDayResolver.getLocalDateTime(gmtJulDay), loc)
    val hour = lmt.get(ChronoField.HOUR_OF_DAY)
    return if (hour in 6..17)
      DayNight.DAY
    else
      DayNight.NIGHT
  }
}
