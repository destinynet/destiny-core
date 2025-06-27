/**
 * Created by smallufo on 2025-06-17.
 */
package destiny.core.astrology.prediction

import destiny.core.IBirthDataNamePlace
import destiny.core.RequestDto
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate


/**
 * 針對一段時間 (month , year)
 */
@RequestDto
@Serializable
data class PeriodRequestDto(
  @Serializable(with = IBirthDataNamePlaceSerializer::class)
  @SerialName("birth_data_name_place")
  val bdnp: IBirthDataNamePlace,
  @Serializable(with = LocalDateSerializer::class)
  val today: LocalDate,
  val current: ReturnDto,
  val next: ReturnDto?,
)
