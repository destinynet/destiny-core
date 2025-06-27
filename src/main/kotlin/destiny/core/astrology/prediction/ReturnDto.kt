/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IHoroscopeDto
import destiny.core.astrology.Synastry
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


interface ReturnDto {
  val horoscope: IHoroscopeDto
  val synastry: Synastry
  val from: GmtJulDay
  val to: GmtJulDay
}

@Serializable
data class SolarReturnDto(
  @SerialName("solarReturn")
  override val horoscope: IHoroscopeDto,
  override val synastry: Synastry,
  @Contextual
  override val from: GmtJulDay,
  @Contextual
  override val to: GmtJulDay
) : ReturnDto

@Serializable
data class LunarReturnDto(
  @SerialName("lunarReturn")
  override val horoscope: IHoroscopeDto,
  override val synastry: Synastry,
  @Contextual
  override val from: GmtJulDay,
  @Contextual
  override val to: GmtJulDay
) : ReturnDto
