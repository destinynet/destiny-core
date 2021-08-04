/**
 * Created by smallufo on 2017-09-25.
 */
package destiny.core.calendar

import java.time.Instant
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime

/**
 * 從 julian day 轉換成各個曆法表示的介面
 */
interface JulDayResolver {

  fun getLocalDateTime(gmtJulDay: GmtJulDay): ChronoLocalDateTime<*>

  fun getLocalDateTime(gmtJulDay: Double): ChronoLocalDateTime<*> {
    return getLocalDateTime(GmtJulDay(gmtJulDay))
  }

  /** 從 gmt instant 轉為 GMT Time  */
  fun getLocalDateTimeFromInstant(gmtInstant: Instant): ChronoLocalDateTime<*>

  fun getDateAndTime(gmtJulDay: GmtJulDay): Pair<ChronoLocalDate, LocalTime> {
    val dateTime = getLocalDateTime(gmtJulDay)
    return Pair(dateTime.toLocalDate(), dateTime.toLocalTime())
  }

}
