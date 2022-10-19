/**
 * Created by smallufo on 2022-10-11.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.TimeTools.toGmtJulDay
import java.io.Serializable
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import javax.inject.Named


/**
 * 單純以時鐘劃分 行星時
 */
@Named
class PlanetaryHourClockImpl : IPlanetaryHour, Serializable {

  override fun getHourIndexOfDay(gmtJulDay: GmtJulDay, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig): IPlanetaryHour.HourIndexOfDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val hour = lmt.get(ChronoField.HOUR_OF_DAY)

    val dayNight = if (hour in 6..17)
      DayNight.DAY
    else
      DayNight.NIGHT

    val hourIndexAfterSunrise =
      if (hour >= 6)
        hour - 5
      else
        hour + 19

    val hourStart = lmt.with(ChronoField.HOUR_OF_DAY, hour.toLong())
      .with(ChronoField.MINUTE_OF_HOUR, 0)
      .with(ChronoField.SECOND_OF_MINUTE, 0)
      .with(ChronoField.MICRO_OF_SECOND, 0)
      .with(ChronoField.MILLI_OF_SECOND, 0)
    val hourEnd = hourStart.plus(1, ChronoUnit.HOURS)

    val dayOfWeek = lmt.let {
      if (hour < 6)
        lmt.minus(6, ChronoUnit.HOURS)
      else
        lmt
    }.get(ChronoField.DAY_OF_WEEK)

    return IPlanetaryHour.HourIndexOfDay(hourStart.toGmtJulDay(loc), hourEnd.toGmtJulDay(loc), hourIndexAfterSunrise, dayNight, dayOfWeek)
  }

}
