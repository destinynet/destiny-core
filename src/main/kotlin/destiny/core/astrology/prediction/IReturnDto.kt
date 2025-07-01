/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IHoroscopeDto
import destiny.core.astrology.Synastry
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

enum class ReturnType {
  SOLAR,
  LUNAR
}


interface IReturnDto {
  val horoscope: IHoroscopeDto
  val returnType: ReturnType
  val synastry: Synastry
  val validFrom: GmtJulDay
  val validTo: GmtJulDay
}

@Serializable
data class ReturnDto(
  override val returnType: ReturnType,
  override val horoscope: IHoroscopeDto,
  override val synastry: Synastry,
  @Contextual
  override val validFrom: GmtJulDay,
  @Contextual
  override val validTo: GmtJulDay
) : IReturnDto

