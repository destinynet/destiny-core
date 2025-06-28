/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.electional

import destiny.core.IAggregatedEvent
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
enum class Span {
  DAY,
  HOURS, // 數小時
  INSTANT
}

interface IEventDto : Comparable<IEventDto> {
  val event: IAggregatedEvent
  val begin: GmtJulDay
  val end: GmtJulDay?
  val span: Span
  val impact: Impact

  override fun compareTo(other: IEventDto): Int {
    return begin.compareTo(other.begin)
  }
}

@Serializable
data class Daily(
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  val allDayEvents: List<IEventDto>,
  val hourEvents: Map<@Serializable(with = LocalDateTimeSerializer::class) LocalDateTime, List<IEventDto>>
) : Comparable<Daily> {
  override fun compareTo(other: Daily): Int {
    return localDate.compareTo(other.localDate)
  }
}


