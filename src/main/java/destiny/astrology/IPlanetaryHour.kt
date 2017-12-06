/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime

/**
 * 取得當下、當地的「行星時」 Planetary Hour
 * 參考資料
 *
 * http://pansci.asia/archives/126644
 *
 * http://www.astrology.com.tr/planetary-hours.asp
 * 晝夜、分別劃分 12等分
 */
interface IPlanetaryHour {

  fun getPlanetaryHour(gmtJulDay: Double, loc: Location): PlanetaryHour

  fun getPlanetaryHour(lmt: ChronoLocalDateTime<*>, loc: Location): Planet {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getPlanetaryHour(gmtJulDay, loc).planet
  }

  fun getPlanetaryHours(fromGmt: Double, toGmt: Double, loc: Location): List<PlanetaryHour>

  fun getPlanetaryHours(fromLmt: ChronoLocalDateTime<*>, toLmt: ChronoLocalDateTime<*>, loc: Location): List<PlanetaryHour> {
    val fromGmt = TimeTools.getGmtJulDay(fromLmt, loc)
    val toGmt = TimeTools.getGmtJulDay(toLmt, loc)
    return getPlanetaryHours(fromGmt, toGmt, loc)
  }
}
