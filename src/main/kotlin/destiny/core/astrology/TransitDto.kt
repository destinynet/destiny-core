/**
 * Created by smallufo on 2025-06-19.
 */
package destiny.core.astrology

import destiny.core.RequestDto
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


@RequestDto
@Serializable
data class TransitDto(
  val natal: Natal,
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  val aspects: List<SynastryAspect>
)
