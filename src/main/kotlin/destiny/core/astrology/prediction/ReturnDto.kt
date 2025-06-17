/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IHoroscopeDto
import destiny.core.astrology.Synastry
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


interface ReturnDto {
  val horoscope: IHoroscopeDto
  val synastry: Synastry
  val from: LocalDateTime
  val to: LocalDateTime
}

@Serializable
data class SolarReturnDto(
  @SerialName("solarReturn")
  override val horoscope: IHoroscopeDto,
  override val synastry: Synastry,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val from: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val to: LocalDateTime
) : ReturnDto

@Serializable
data class LunarReturnDto(
  @SerialName("lunarReturn")
  override val horoscope: IHoroscopeDto,
  override val synastry: Synastry,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val from: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val to: LocalDateTime
) : ReturnDto
