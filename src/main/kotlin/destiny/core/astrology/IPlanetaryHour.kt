/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.astrology.Planet.*
import destiny.core.calendar.Constants
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.tools.KotlinLogging
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 取得當下、當地的「行星時」 Planetary Hour
 * 參考資料
 *
 * http://pansci.asia/archives/126644
 *
 */
interface IPlanetaryHour {

  /**
   * @param hourIndexAfterSunrise : 日出後的第幾個小時 , from 1 to 24
   * @param dayOfWeek : 1:星期一 , 2:星期二 ... , 6:星期六 , 7:星期日
   **/
  fun getPlanet(hourIndexAfterSunrise: Int, dayOfWeek: Int): Planet {

    require(hourIndexAfterSunrise in 1..24)
    require(dayOfWeek in 1..7) {
      "dayOfWeek($dayOfWeek) should be between 1 (Monday) and 7 (Sunday)"
    }

    // 星期六白天起，七顆行星順序： 土、木、火、日、金、水、月
    val seqPlanet = arrayOf(SATURN, JUPITER, MARS, SUN, VENUS, MERCURY, MOON)

    // 日期順序
    val seqDay = intArrayOf(6, 7, 1, 2, 3, 4, 5)

    logger.trace { "dayOfWeek = $dayOfWeek" }

    logger.trace { "hourIndexAfterSunrise = $hourIndexAfterSunrise" }

    // from 0 to 6
    val indexOfDayTable = seqDay.indexOf(dayOfWeek)
    logger.trace { "indexOfDayTable = $indexOfDayTable" }

    // 0 to (24x7-1)
    val hourIndexFromSaturday = indexOfDayTable * 24 + (hourIndexAfterSunrise - 1)
    logger.trace { "hourIndexFromSaturday = $hourIndexFromSaturday" }

    return seqPlanet[hourIndexFromSaturday % 7]
  }

  data class HourIndexOfDay(
    val hourStart: GmtJulDay,
    val hourEnd: GmtJulDay,
    /** 日出之後 的 hour index , from 1 to 24 */
    val hourIndexAfterSunrise: Int,
    val dayNight: DayNight,
    /** Monday : 1 ... Sunday : 7 */
    val dayOfWeek: Int
  )

  fun getHourIndexOfDay(gmtJulDay: GmtJulDay, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()): HourIndexOfDay?

  fun getPlanetaryHour(gmtJulDay: GmtJulDay, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()): PlanetaryHour? {
    return getHourIndexOfDay(gmtJulDay, loc, julDayResolver, transConfig)?.let { t: HourIndexOfDay ->
      val planet = getPlanet(t.hourIndexAfterSunrise, t.dayOfWeek)
      PlanetaryHour(t.hourStart, t.hourEnd, t.dayNight, planet, loc)
    }
  }

  fun getPlanetaryHour(lmt: ChronoLocalDateTime<*>, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()): Planet? {
    return getPlanetaryHour(lmt.toGmtJulDay(loc), loc, julDayResolver, transConfig)?.planet
  }

  fun getPlanetaryHours(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()): List<PlanetaryHour> {

    require(fromGmt < toGmt) {
      "fromGmt : $fromGmt larger than or equal to toGmt : $toGmt"
    }

    fun fromGmtToPlanetaryHour(gmt: GmtJulDay): PlanetaryHour? {
      return getHourIndexOfDay(gmt, loc, julDayResolver, transConfig)?.let { r ->
        val planet = getPlanet(r.hourIndexAfterSunrise, r.dayOfWeek)
        PlanetaryHour(r.hourStart, r.hourEnd, r.dayNight, planet, loc)
      }
    }

    return generateSequence(fromGmtToPlanetaryHour(fromGmt)) {
      fromGmtToPlanetaryHour(it.hourEnd + (1 / Constants.SECONDS_OF_DAY.toDouble()))
    }.takeWhile { it.hourStart < toGmt }
      .toList()
  }

  fun getPlanetaryHours(fromLmt: ChronoLocalDateTime<*>, toLmt: ChronoLocalDateTime<*>, loc: ILocation, julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()): List<PlanetaryHour> {
    return getPlanetaryHours(fromLmt.toGmtJulDay(loc), toLmt.toGmtJulDay(loc), loc, julDayResolver, transConfig)
  }

  fun getDailyPlanetaryHours(date : ChronoLocalDate , loc: ILocation , julDayResolver: JulDayResolver , transConfig: TransConfig = TransConfig()) : List<PlanetaryHour> {
    val fromLmt = date.atTime(LocalTime.MIDNIGHT)
    val toLmt = fromLmt.plus(1L , ChronoUnit.DAYS)
    return getPlanetaryHours(fromLmt, toLmt, loc, julDayResolver, transConfig)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
