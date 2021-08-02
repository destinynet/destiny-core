/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinInstant


/** Julian Day of 1970-01-01 */
const val EPOCH_JULIAN_DAY = 2440587.5

/** 1970-01-01 距離 Julian Day 幾秒 */
const val EPOCH_SECONDS = (2440587.5 * 86400L).toLong()

/** 承上，幾 milliSeconds */
const val EPOCH_MILLI_SECONDS = EPOCH_SECONDS * 1000L

@JvmInline
value class GmtJulDay(val value: Double) {

  fun toInstant(): Instant {
    return ((value - EPOCH_JULIAN_DAY) * 86400L).let { secDouble ->
      val second = secDouble.toLong()
      val nano = ((secDouble - second) * 1_000_000_000).toInt()
      Instant.fromEpochSeconds(second, nano)
    }
  }

}

fun Instant.toGmtJulDay(): GmtJulDay {
  val millis = this.toEpochMilliseconds()
  return GmtJulDay((millis + EPOCH_MILLI_SECONDS) / (86400 * 1000).toDouble())
}

/** Experimental KMM for [JulDayResolver] */
fun interface IJulDayResolver : (Instant) -> Pair<LocalDateTime, CalType> {

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

    /** 承上 , 距離秒數 : -12219292800 */
    const val GREGORIAN_START_EPOCH_SECONDS = GREGORIAN_START_EPOCH_DAYS * 86400L

    /** 承上 , 距離多少 milliSeconds */
    const val GREGORIAN_START_EPOCH_MILLISECONDS = GREGORIAN_START_EPOCH_SECONDS * 1000L
  }
}

fun IJulDayResolver.convert(jInstant: java.time.Instant): Pair<LocalDateTime, CalType> {
  return this.invoke(jInstant.toKotlinInstant())
}


