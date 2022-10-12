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
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import javax.inject.Named


/**
 * 單純以時鐘劃分 行星時
 */
@Named
class PlanetaryHourClockImpl(private val julDayResolver: JulDayResolver) : IPlanetaryHour {

  override fun getHourIndexOfDay(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): IPlanetaryHour.HourIndexOfDay? {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val hourIndex = lmt.get(ChronoField.HOUR_OF_DAY) + 1
    val hourStart = lmt.with(ChronoField.HOUR_OF_DAY, hourIndex.toLong() - 1)
      .with(ChronoField.SECOND_OF_MINUTE, 0)
      .with(ChronoField.MICRO_OF_SECOND, 0)
      .with(ChronoField.MILLI_OF_SECOND, 0)
    val hourEnd = hourStart.plus(1, ChronoUnit.HOURS)
    val dayNight = if (hourIndex in 6..17)
      DayNight.DAY
    else
      DayNight.NIGHT

    return IPlanetaryHour.HourIndexOfDay(hourStart.toGmtJulDay(loc), hourEnd.toGmtJulDay(loc), hourIndex, dayNight)
  }

  override fun getPlanetaryHour(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): PlanetaryHour? {
    TODO("Not yet implemented")
  }

  override fun getPlanetaryHours(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, transConfig: TransConfig): List<PlanetaryHour> {
    TODO("Not yet implemented")
  }
}
