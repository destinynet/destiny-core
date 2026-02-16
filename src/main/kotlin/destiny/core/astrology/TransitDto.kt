/**
 * Created by smallufo on 2025-06-19.
 */
package destiny.core.astrology

import destiny.core.RequestDto
import destiny.core.Scale
import destiny.core.astrology.prediction.*
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalTimeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime


@RequestDto
@Serializable
data class TransitDto(
  val natal: Natal,
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  val aspects: List<SynastryAspect>
)

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


@Serializable
data class ProgressionDto(
  val horoscope: IHoroscopeDto,
  val synastry: Synastry
)


@Serializable
@RequestDto
data class EventModel(
  val natal: IPersonHoroscopeDto,
  val grain: BirthDataGrain,
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  @Serializable(with = LocalTimeSerializer::class)
  val localTime: LocalTime?,
  val solarArcModel: ISolarArcModel,
  val transitToSolarArcAspects: List<SynastryAspect>,
  val transitToSecondaryAspects: List<SynastryAspect>,
  val firdaria: Firdaria?,
  val profectionMap: Map<Scale, Profection>,
  val surroundingEvents: List<@Contextual ITimeLineEvent>,
  val surroundingLunarReturns: List<ReturnCoverageDto>,
  @SerialName("transit")
  val transit: ProgressionDto,
  @SerialName("secondary_progression")
  val secondaryProgression: ProgressionDto,
  @SerialName("tertiary_progression")
  val tertiaryProgression: ProgressionDto,
)
