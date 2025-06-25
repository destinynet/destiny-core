/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.ISolarArcModel
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


@Serializable
data class TransitSolarArcModel(
  val natal: IPersonHoroscopeDto,
  val grain: Grain,
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  val solarArcModel: ISolarArcModel,
  val transitToSolarArcAspects: List<SynastryAspect>
) {
  enum class Grain {
    DAY , MINUTE
  }
}
