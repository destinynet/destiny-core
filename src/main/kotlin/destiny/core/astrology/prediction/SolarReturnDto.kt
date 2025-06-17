/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IHoroscopeDto
import destiny.core.astrology.Synastry
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class SolarReturnDto(
  val year: Int,
  val solarReturn: IHoroscopeDto,
  val synastry: Synastry,
  @Serializable(with = LocalDateTimeSerializer::class)
  val from: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  val to: LocalDateTime
)

@Serializable
data class LunarReturnDto(
  val year: Int,
  val month: Int,
  val lunarReturn: IHoroscopeDto,
  val synastry: Synastry,
  @Serializable(with = LocalDateTimeSerializer::class)
  val from: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  val to: LocalDateTime
)
