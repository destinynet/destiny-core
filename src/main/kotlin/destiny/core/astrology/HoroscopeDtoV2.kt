/**
 * Created by smallufo on 2026-05-14.
 *
 * V2 Horoscope DTO — strict superset of V1, adds:
 *  - mundane    : horizon-based positions for each point (alt/az/HA/declination)
 *  - arabicLots : Hellenistic Lots (Fortune, Spirit, Eros, Victory, Necessity, Courage, Nemesis)
 *  - antiscia   : Phase 1.5 placeholder (default emptyMap)
 *
 * Azimuth convention: N=0, E=90, S=180, W=270 (post-normalize, matches destiny-core AzimuthImpl).
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
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Horizon-based (mundane) position of an astrological point at the birth moment.
 * Azimuth uses N=0/E=90/S=180/W=270 (destiny-core convention, post-normalize).
 */
@Serializable
data class MundanePosition(
  /** True altitude in degrees, horizon = 0 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val altitude: Double,
  /** Azimuth in degrees, N=0 clockwise */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val azimuth: Double,
  /** Hour angle in hours, normalized to [-12, +12]. Positive = west of meridian. */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val hourAngle: Double,
  /** Declination in degrees */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val declination: Double,
)

/**
 * Information about an Arabic Lot (Hellenistic Part).
 */
@Serializable
data class ArabicLotInfo(
  val lot: Arabic,
  @Serializable(with = IZodiacDegreeSerializer::class)
  val signDegree: IZodiacDegree,
  /** House (1-12), null if grain is DAY or not computable */
  val house: Int?,
  /** Aspects with other points in the chart */
  val astroAspects: List<@Serializable(with = IPointAspectPatternSerializer::class) IPointAspectPattern>,
)

/**
 * Antiscia / contra-antiscia point pair (Phase 1.5 placeholder).
 */
@Serializable
data class AntisciaPair(
  @Serializable(with = IZodiacDegreeSerializer::class)
  val antiscia: IZodiacDegree,
  @Serializable(with = IZodiacDegreeSerializer::class)
  val contraAntiscia: IZodiacDegree,
  val aspects: List<@Serializable(with = IPointAspectPatternSerializer::class) IPointAspectPattern> = emptyList(),
)

/**
 * V2 DTO for [IHoroscopeModel] — strict superset of [IHoroscopeDto].
 */
interface IHoroscopeDtoV2 : IHoroscopeDto {
  val mundanePositions: Map<AstroPoint, MundanePosition>
  val arabicLots: Map<Arabic, ArabicLotInfo>
  /** Phase 1.5 — default empty until activated */
  val antiscia: Map<AstroPoint, AntisciaPair>
}

interface IPersonHoroscopeDtoV2 : IHoroscopeDtoV2, IPersonHoroscopeDto


@Serializable
data class HoroscopeDtoV2(
  // ===== V1 既有 17 個 field（必須全部 override） =====
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
  override val harmonics: Map<Int, Harmonic>,
  // ===== V2 新增 =====
  override val mundanePositions: Map<AstroPoint, MundanePosition> = emptyMap(),
  override val arabicLots: Map<Arabic, ArabicLotInfo> = emptyMap(),
  override val antiscia: Map<AstroPoint, AntisciaPair> = emptyMap(),
) : IHoroscopeDtoV2 {

  /** 從既有 v1 DTO + v2 extras 組合 */
  constructor(
    v1: IHoroscopeDto,
    mundanePositions: Map<AstroPoint, MundanePosition>,
    arabicLots: Map<Arabic, ArabicLotInfo>,
    antiscia: Map<AstroPoint, AntisciaPair> = emptyMap(),
  ) : this(
    v1.time, v1.utc, v1.location, v1.place,
    v1.signs, v1.houses, v1.stars, v1.axisStars,
    v1.houseStarDistribution, v1.elementPercentage, v1.qualityPercentage,
    v1.tightestAspects, v1.astroPatterns, v1.classicalAstrologyPatterns,
    v1.graphPatterns, v1.midPoints, v1.harmonics,
    mundanePositions, arabicLots, antiscia,
  )
}


/**
 * V2 person-horoscope DTO. Mirrors [Natal] structure plus v2 extras.
 */
@RequestDto
@Serializable
data class NatalV2(
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
  override val harmonics: Map<Int, Harmonic>,
  // V2 extras
  override val mundanePositions: Map<AstroPoint, MundanePosition> = emptyMap(),
  override val arabicLots: Map<Arabic, ArabicLotInfo> = emptyMap(),
  override val antiscia: Map<AstroPoint, AntisciaPair> = emptyMap(),
) : IPersonHoroscopeDtoV2 {

  constructor(
    gender: Gender, age: Int?, name: String?, v1: IHoroscopeDto,
    mundanePositions: Map<AstroPoint, MundanePosition>,
    arabicLots: Map<Arabic, ArabicLotInfo>,
    antiscia: Map<AstroPoint, AntisciaPair> = emptyMap(),
  ) : this(
    gender, age, name,
    v1.time, v1.utc, v1.location, v1.place,
    v1.signs, v1.houses, v1.stars, v1.axisStars,
    v1.houseStarDistribution, v1.elementPercentage, v1.qualityPercentage,
    v1.tightestAspects, v1.astroPatterns, v1.classicalAstrologyPatterns,
    v1.graphPatterns, v1.midPoints, v1.harmonics,
    mundanePositions, arabicLots, antiscia,
  )
}
