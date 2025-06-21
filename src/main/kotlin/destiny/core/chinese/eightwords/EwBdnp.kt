/**
 * Created by smallufo on 2025-03-15.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.RequestDto
import destiny.core.Scale
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.electional.Dtos
import destiny.tools.serializers.GenderSerializer
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Bdnp stands for Birth-Data-Name-Place
 */
@RequestDto
@Serializable
data class EwBdnp(
  @Serializable(with = GenderSerializer::class)
  override val gender: Gender,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val time: LocalDateTime,
  @Serializable(with = ILocationSerializer::class)
  override val location: ILocation,
  val age: Int?,
  override val name: String?,
  override val place: String?,
  @SerialName("生肖")
  val animal: String,
  @SerialName("命宮干支")
  val risingStemBranch: StemBranch,
  @SerialName("上升星座")
  val risingSign: ZodiacSign,
  @SerialName("八字")
  val ew: Map<Scale, String>,
  @SerialName("八字特徵")
  val notes: Set<Dtos.EwEvent.EwIdentity>,
  @SerialName("納音")
  val nayin: Map<Scale, String>,
  @SerialName("空亡")
  val voids: Map<Scale, String>,
  @SerialName("八字干支")
  val ewDetails: Map<Scale, StemAndBranch>,
  @SerialName("節氣資訊")
  val solarTermsPos: String,
  @SerialName("大運")
  val fortuneLarges: List<FortuneLarge>,
  @SerialName("日主分數（八分法，滿分八分）")
  val score: Double,
  @SerialName("八分法解釋")
  val scoreDescription: String? = null,
  val recentYears: List<YearData>
) : IBirthDataNamePlace {

  @Serializable
  data class StemReaction(
    val stem: Stem,
    @SerialName("十神")
    val relation: String
  )

  @Serializable
  data class StemAndBranch(
    @SerialName("天干")
    val stem: StemReaction,
    @SerialName("地支")
    val branch: Branch,
    @SerialName("地支藏干")
    val hiddenStems: List<StemReaction>
  )

  /**
   * 大運
   */
  @Serializable
  data class FortuneLarge(
    @SerialName("isCurrent")
    val current: Boolean,
    @SerialName("干支")
    val stemBranch: IStemBranch,
    @SerialName("十神藏干")
    val stemAndBranch: StemAndBranch,
    @SerialName("大運特徵")
    val notes: Set<Dtos.EwEvent.EwFlow>,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    val startAge: Int,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    val endAge: Int,
  )

  /**
   * 流年
   */
  @Serializable
  data class YearData(
    val year: Int,
    @SerialName("isCurrent")
    val current: Boolean,
    @SerialName("干支")
    val stemBranch: IStemBranch,
    @SerialName("十神藏干")
    val stemAndBranch: StemAndBranch,
    @SerialName("流年特徵")
    val notes: Set<Dtos.EwEvent.EwFlow>
  )
}
