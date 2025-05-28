package destiny.core.misc

import destiny.tools.serializers.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class TimeRange(
  @Serializable(with = LocalTimeSerializer::class)
  val start: LocalTime,

  @Serializable(with = LocalTimeSerializer::class)
  val end: LocalTime
)
