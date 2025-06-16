/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.IHoroscopeDto
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class AnnualReport(
  val year: Int,
  val solarReturn: IHoroscopeDto,
  val synastryAspects: List<SynastryAspect>,
  @Serializable(with = LocalDateTimeSerializer::class)
  val from: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  val to: LocalDateTime,
)
