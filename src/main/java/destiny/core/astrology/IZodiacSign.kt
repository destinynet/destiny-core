/**
 * Created by smallufo on 2018-06-22.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

data class ZodiacSignModel(
  val current: Pair<ZodiacSign, GmtJulDay>,
  val next: Pair<ZodiacSign, GmtJulDay>
)

interface IZodiacSign {

  /**
   * 取得此星體，位於哪個星座之間
   * 畫過 之前(目前)的星座 於何時
   * 即將到未來的星座 於何時
   */
  fun getSignsBetween(star: Star, gmtJulDay: GmtJulDay): ZodiacSignModel

  fun getSign(star: Star, gmtJulDay: GmtJulDay): ZodiacSign {
    return getSignsBetween(star, gmtJulDay).current.first
  }

  fun getSignsBetween(star: Star, lmt: ChronoLocalDateTime<*>, location: ILocation): ZodiacSignModel {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    return getSignsBetween(star, gmtJulDay)
  }
}

