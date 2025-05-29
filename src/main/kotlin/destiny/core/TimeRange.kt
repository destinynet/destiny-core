package destiny.core

import destiny.tools.serializers.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class TimeRange(
  @Serializable(with = LocalTimeSerializer::class)
  val begin: LocalTime,

  @Serializable(with = LocalTimeSerializer::class)
  val end: LocalTime
)
