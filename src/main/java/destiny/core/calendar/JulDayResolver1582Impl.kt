package destiny.core.calendar

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mu.KotlinLogging

class JulDayResolver1582Impl : IJulDayResolver {

  private val logger = KotlinLogging.logger { }

  override fun invoke(instant: Instant): LocalDateTime {

    val ms = instant.toEpochMilliseconds()
    return if (ms >= IJulDayResolver.GREGORIAN_START_EPOCH_MILLISECONDS) {
      instant.toLocalDateTime(TimeZone.UTC)
    } else {
      val isGregorian = false



      TODO()
    }
  }

}
