/**
 * Created by smallufo on 2018-06-22.
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

interface IZodiacSign {

  /**
   * 取得此星體，位於哪個星座之間
   * 畫過 之前(目前)的星座 於何時
   * 即將到未來的星座 於何時
   */
  fun getSignsBetween(star: Star, gmtJulDay: Double): Pair<Pair<ZodiacSign, Double>, Pair<ZodiacSign, Double>>

  fun getSignsBetween(star: Star, lmt : ChronoLocalDateTime<*> , location : ILocation): Pair<Pair<ZodiacSign, Double>, Pair<ZodiacSign, Double>> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt , location)
    return getSignsBetween(star , gmtJulDay)
  }
}

