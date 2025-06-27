/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.RequestDto
import destiny.core.astrology.prediction.ISolarArcModel
import destiny.core.astrology.prediction.ITransitModel
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


@Serializable
@RequestDto
data class TransitSolarArcModel(
  val natal: IPersonHoroscopeDto,
  val grain: BirthDataGrain,
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  val solarArcModel: ISolarArcModel,
  val transitToSolarArcAspects: List<SynastryAspect>
) : ITransitModel by solarArcModel
