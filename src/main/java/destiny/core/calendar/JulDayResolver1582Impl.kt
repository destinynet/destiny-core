package destiny.core.calendar

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.floor

class JulDayResolver1582Impl : IJulDayResolver {

  override fun invoke(instant: Instant): Pair<LocalDateTime, CalType> {

    val ms = instant.toEpochMilliseconds()
    return if (ms >= IJulDayResolver.GREGORIAN_START_EPOCH_MILLISECONDS) {
      instant.toLocalDateTime(TimeZone.UTC) to CalType.GREGORIAN
    } else {
      val gmtJulDay = instant.toGmtJulDay()

      val u0: Double = gmtJulDay.value + 32082.5

      val u2: Double = floor(u0 + 123.0)
      val u3: Double = floor((u2 - 122.2) / 365.25)
      val u4: Double = floor((u2 - floor(365.25 * u3)) / 30.6001)
      var month = (u4 - 1.0).toInt()
      if (month > 12) {
        month -= 12
      }
      val day = (u2 - floor(365.25 * u3) - floor(30.6001 * u4)).toInt()
      val y = (u3 + floor((u4 - 2.0) / 12.0) - 4800).toInt()

      var ad = true
      val year: Int

      if (y <= 0) {
        ad = false
        year = -(y - 1) // 取正值
      } else {
        year = y
      }

      val h = (gmtJulDay.value - floor(gmtJulDay.value + 0.5) + 0.5) * 24.0
      val hour = h.toInt()
      val minute = (h * 60 - hour * 60).toInt()
      val second = h * 3600 - (hour * 3600).toDouble() - (minute * 60).toDouble()

      val (secsInt, nanoInt) = TimeTools.splitSecond(second)

      val prolepticYear = TimeTools.getNormalizedYear(ad, year)

      return LocalDateTime(prolepticYear, month, day, hour, minute, secsInt, nanoInt) to CalType.JULIAN
    }
  }

}
