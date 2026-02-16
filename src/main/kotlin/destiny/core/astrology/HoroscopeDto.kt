/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.Graph
import destiny.core.IBirthDataNamePlace
import destiny.core.RequestDto
import destiny.core.astrology.Natal.HouseStarDistribution
import destiny.core.astrology.Natal.StarPosInfo
import destiny.core.calendar.ILocation
import destiny.tools.serializers.*
import destiny.tools.serializers.astrology.IMidPointWithFocalSerializer
import destiny.tools.serializers.astrology.IPointAspectPatternSerializer
import destiny.tools.serializers.astrology.RetrogradePhaseWithDescriptionSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * DTO for [IHoroscopeModel]
 */
interface IHoroscopeDto {
  val time: LocalDateTime
  val utc: LocalDateTime
  val location: ILocation
  val place: String?
  val signs: Map<ZodiacSign, List<AstroPoint>>
  val houses: List<HouseDto>
  val stars: Map<AstroPoint, StarPosInfo>
  val axisStars : Map<Axis, List<AxisStar>>
  val houseStarDistribution: Map<HouseType, HouseStarDistribution>
  val elementPercentage: Map<Element, Double>
  val qualityPercentage: Map<Quality, Double>
  val tightestAspects: List<IPointAspectPattern>
  val astroPatterns: List<AstroPattern>
  val classicalAstrologyPatterns: List<String>
  val graphPatterns: Graph<Planet>
  val midPoints: List<IMidPointWithFocal>
  val harmonics: Map<Int, Harmonic>
}


@Serializable
data class HoroscopeDto(
  @Serializable(with = LocalDateTimeSerializer::class)
  override val time: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val utc: LocalDateTime,
  @Serializable(with = ILocationSerializer::class)
  override val location: ILocation,
  override val place: String?,
  override val signs: Map<ZodiacSign, List<AstroPoint>> = emptyMap(),
  override val houses: List<HouseDto> = emptyList(),
  override val stars: Map<AstroPoint, StarPosInfo> = emptyMap(),
  override val axisStars: Map<Axis, List<AxisStar>>,
  override val houseStarDistribution: Map<HouseType, HouseStarDistribution>,
  override val elementPercentage: Map<Element, @Serializable(with = DoubleTwoDecimalSerializer::class) Double>,
  override val qualityPercentage: Map<Quality, @Serializable(with = DoubleTwoDecimalSerializer::class) Double>,
  override val tightestAspects: List<@Serializable(with = IPointAspectPatternSerializer::class) IPointAspectPattern>,
  override val astroPatterns: List<AstroPattern> = emptyList(),
  override val classicalAstrologyPatterns: List<String> = emptyList(),
  @Serializable(with = GraphPlanetSerializer::class)
  override val graphPatterns: Graph<Planet>,
  override val midPoints: List<@Serializable(with = IMidPointWithFocalSerializer::class) IMidPointWithFocal> = emptyList(),
  override val harmonics: Map<Int, Harmonic>
) : IHoroscopeDto

interface IPersonHoroscopeDto : IHoroscopeDto, IBirthDataNamePlace

/**
 * a DTO object for a [IPersonHoroscopeModel]
 */
@RequestDto
@Serializable
data class Natal(
  @Serializable(with = GenderSerializer::class)
  override val gender: Gender,
  val age: Int?,
  override val name: String?,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val time: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val utc: LocalDateTime,
  @Serializable(with = ILocationSerializer::class)
  override val location: ILocation,
  override val place: String?,
  override val signs: Map<ZodiacSign, List<AstroPoint>>,
  override val houses: List<HouseDto>,
  override val stars: Map<AstroPoint, StarPosInfo>,
  override val axisStars: Map<Axis, List<AxisStar>>,
  override val houseStarDistribution: Map<HouseType, HouseStarDistribution>,
  override val elementPercentage: Map<Element, @Serializable(with = DoubleTwoDecimalSerializer::class) Double>,
  override val qualityPercentage: Map<Quality, @Serializable(with = DoubleTwoDecimalSerializer::class) Double>,
  override val tightestAspects: List<@Serializable(with = IPointAspectPatternSerializer::class) IPointAspectPattern>,
  override val astroPatterns: List<AstroPattern>,
  override val classicalAstrologyPatterns: List<String>,
  @Serializable(with = GraphPlanetSerializer::class)
  override val graphPatterns: Graph<Planet>,
  override val midPoints: List<@Serializable(with = IMidPointWithFocalSerializer::class) IMidPointWithFocal>,
  override val harmonics: Map<Int, Harmonic>
) : IPersonHoroscopeDto {
  constructor(gender: Gender, age: Int, name: String?, dto: IHoroscopeDto) :
    this(
      gender, age, name,
      dto.time, dto.utc, dto.location, dto.place, dto.signs, dto.houses,
      dto.stars, dto.axisStars,
      dto.houseStarDistribution, dto.elementPercentage, dto.qualityPercentage,
      dto.tightestAspects, dto.astroPatterns, dto.classicalAstrologyPatterns, dto.graphPatterns, dto.midPoints,
      dto.harmonics
    )

  @Serializable
  data class StarPosInfo(
    @Serializable(with = IZodiacDegreeSerializer::class)
    val signDegree: IZodiacDegree,
    val element: Element,
    val quality: Quality,
    override val house: Int?,
    val motion: Motion? = null,
    @Serializable(with = RetrogradePhaseWithDescriptionSerializer::class)
    val retrogradePhase: RetrogradePhase? = null,
    val rulingHouses : Set<RulingHouse> = emptySet(),
    /** 定位星資訊 */
    val dispositors: Set<Planet>,
    val astroAspects: List<@Serializable(with = IPointAspectPatternSerializer::class) IPointAspectPattern> = emptyList()
  ) : IStarSummary {
    override val sign: ZodiacSign get() = signDegree.sign
    override val degree: Double get() = signDegree.signDegree.second
    override val isRetrograde: Boolean get() = motion == Motion.RETROGRADE
  }

  @Serializable
  data class HouseStarDistribution(val starCount: Int,
                                   @Serializable(with = DoubleTwoDecimalSerializer::class)
                                   val percentage: Double)
}
