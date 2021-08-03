/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import destiny.core.calendar.Constants.SECONDS_OF_DAY
import destiny.core.calendar.Constants.UnixEpoch.JULIAN_MILLI_SECONDS
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinInstant


@JvmInline
value class GmtJulDay(val value: Double) {

  fun toInstant(): Instant {
    return ((value - Constants.UnixEpoch.JULIAN_DAY) * SECONDS_OF_DAY).let { secDouble ->
      val second = secDouble.toLong()
      val nano = ((secDouble - second) * 1_000_000_000).toInt()
      Instant.fromEpochSeconds(second, nano)
    }
  }

}

fun Instant.toGmtJulDay(): GmtJulDay {
  val millis = this.toEpochMilliseconds()
  return GmtJulDay((millis + JULIAN_MILLI_SECONDS) / (SECONDS_OF_DAY * 1000).toDouble())
}

/** Experimental KMM for [JulDayResolver] */
fun interface IJulDayResolver : (Instant) -> Pair<LocalDateTime, CalType> {

  companion object

}

fun IJulDayResolver.convert(jInstant: java.time.Instant): Pair<LocalDateTime, CalType> {
  return this.invoke(jInstant.toKotlinInstant())
}


