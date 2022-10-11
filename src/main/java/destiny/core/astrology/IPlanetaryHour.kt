/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
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

  fun getPlanetaryHour(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig = TransConfig()): PlanetaryHour?

  fun getPlanetaryHour(lmt: ChronoLocalDateTime<*>, loc: ILocation, transConfig: TransConfig = TransConfig()): Planet? {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getPlanetaryHour(gmtJulDay, loc, transConfig)?.planet
  }

  fun getPlanetaryHours(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, transConfig: TransConfig = TransConfig()): List<PlanetaryHour>

  fun getPlanetaryHours(fromLmt: ChronoLocalDateTime<*>, toLmt: ChronoLocalDateTime<*>, loc: ILocation, transConfig: TransConfig = TransConfig()): List<PlanetaryHour> {
    val fromGmt = TimeTools.getGmtJulDay(fromLmt, loc)
    val toGmt = TimeTools.getGmtJulDay(toLmt, loc)
    return getPlanetaryHours(fromGmt, toGmt, loc, transConfig)
  }
}
