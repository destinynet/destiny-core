/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinInstant

/**
 * Experimental KMM for [JulDayResolver]
 */

/** Julian Day of 1970-01-01 */
const val EPOCH_JULIAN_DAY = 2440587.5

fun julDayToInstant(gmtJulDay: Double): Instant {
  return ((gmtJulDay - EPOCH_JULIAN_DAY) * 86400L).let { secDouble ->
    val second = secDouble.toLong()
    val nano = ((secDouble - second) * 1_000_000_000).toInt()
    Instant.fromEpochSeconds(second, nano)
  }
}

fun interface IJulDayResolver : (Instant) -> LocalDateTime {

  companion object {
    /**
     * Julian Calendar    終止於西元 1582-10-04 , 該日的 Julian Day 是 2299159.5
     * Gregorian Calendar 開始於西元 1582-10-15 , 該日的 Julian Day 是 2299160.5
     */

    const val GREGORIAN_START_JULIAN_DAY = 2299160.5

    /**
     * UNIX 開始日，往前推算到 Gregorian 開始日 (西元 1582-10-15) , 倒數 141427 整天
     */
    const val GREGORIAN_START_EPOCH_DAYS = -141427

    /** 承上 , 距離秒數 */
    const val GREGORIAN_START_EPOCH_SECONDS = GREGORIAN_START_EPOCH_DAYS * 86400L

    /** 承上 , 距離多少 milliSeconds */
    const val GREGORIAN_START_EPOCH_MILLISECONDS = GREGORIAN_START_EPOCH_SECONDS * 1000L
  }
}

fun IJulDayResolver.convert(jInstant: java.time.Instant): LocalDateTime {
  return this.invoke(jInstant.toKotlinInstant())
}


