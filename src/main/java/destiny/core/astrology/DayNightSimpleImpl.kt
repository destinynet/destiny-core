/**
 * Created by smallufo on 2017-12-24.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.KEY_DAY_NIGHT
import destiny.tools.getDescription
import destiny.tools.getTitle
import java.io.Serializable
import java.time.temporal.ChronoField
import java.util.*

@Impl([Domain(KEY_DAY_NIGHT, DayNightSimpleImpl.VALUE)])
class DayNightSimpleImpl(val julDayResolver: JulDayResolver) : IDayNight, Serializable {

  override fun getDayNight(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): DayNight {

    val lmt = TimeTools.getLmtFromGmt(julDayResolver.getLocalDateTime(gmtJulDay), loc)
    val hour = lmt.get(ChronoField.HOUR_OF_DAY)
    return if (hour in 6..17)
      DayNight.DAY
    else
      DayNight.NIGHT
  }

  override fun toString(locale: Locale): String {
    return DayNightImpl.Simple.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return DayNightImpl.Simple.getDescription(locale)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DayNightSimpleImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


  companion object {
    const val VALUE: String = "simple"
  }


}
