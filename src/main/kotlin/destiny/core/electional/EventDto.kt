/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem
import destiny.core.chinese.eightwords.FlowDtoTransformer
import destiny.core.chinese.eightwords.FlowDtoTransformer.toAffectingDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toToFlowTrilogyDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toTrilogyToFlowDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toAuspiciousDto
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toInauspiciousDto
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toStemRootedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toTrilogyDtos
import kotlinx.serialization.Serializable

sealed interface IAggregatedEvent {
  val description: String
  val impact: Impact
}

@Serializable
class EventDto(
  val event: IAggregatedEvent,
  val begin: GmtJulDay,
  val end: GmtJulDay?
) : Comparable<EventDto> {
  override fun compareTo(other: EventDto): Int {
    // 依據 begin 排序
    return begin.compareTo(other.begin)
  }
}


/** 八字事件 */
@Serializable
sealed class Ew : IAggregatedEvent {

  @Serializable
  data class NatalStems(
    val scales: Set<Scale>,
    val stem: Stem
  )

  @Serializable
  data class NatalBranches(
    val scales: Set<Scale>,
    val branch: Branch
  )

  @Serializable
  sealed class EwIdentity : Ew() {
    override val impact: Impact = Impact.GLOBAL

    /**
     * 本命天干合化
     * DTO for [IdentityPattern.StemCombined]
     * transformed by [IdentityDtoTransformer.toStemCombinedDtos]
     * */
    @Serializable
    data class StemCombinedDto(
      override val description: String,
      val natalStems: Set<NatalStems>,
      val combined: FiveElement
    ) : EwIdentity()

    /**
     * 本命地支六合
     * DTO for [IdentityPattern.BranchCombined]
     * transformed by [IdentityDtoTransformer.toBranchCombinedDtos]
     */
    @Serializable
    data class BranchCombinedDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity()

    /**
     * 本命地支三合
     * DTO for [IdentityPattern.Trilogy]
     * transformed by [IdentityDtoTransformer.toTrilogyDtos]
     *
     */
    @Serializable
    data class TrilogyDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>,
      val trilogy: FiveElement
    ) : EwIdentity()

    /**
     * 本命地支正沖
     * DTO for [IdentityPattern.BranchOpposition]
     * transformed by [IdentityDtoTransformer.toBranchOppositionDtos]
     */
    @Serializable
    data class BranchOppositionDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity()

    /**
     * 本命天干通根
     * DTO for [IdentityPattern.StemRooted]
     * transformed by [IdentityDtoTransformer.toStemRootedDtos]
     */
    @Serializable
    data class StemRootedDto(
      override val description: String,
      val natalStems: Set<NatalStems>,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity()

    /**
     * 吉祥
     * DTO for [IdentityPattern.AuspiciousPattern]
     * transformed by [IdentityDtoTransformer.toAuspiciousDto]
     */
    @Serializable
    data class AuspiciousDto(
      override val description: String,
      val scales: Map<Scale, Set<Auspicious>>
    ) : EwIdentity()

    /**
     * 不祥
     * DTO for [IdentityPattern.InauspiciousPattern]
     * transformed by [IdentityDtoTransformer.toInauspiciousDto]
     */
    @Serializable
    data class InauspiciousDto(
      override val description: String,
      val scales: Map<Scale, Set<Inauspicious>>
    ) : EwIdentity()
  }

  @Serializable
  sealed class EwFlow : Ew() {

    override val impact: Impact = Impact.PERSONAL

    /** 是否與本命時柱相關 */
    abstract val hourRelated: Boolean

    @Serializable
    data class FlowStems(
      val scales: Set<FlowScale>,
      val stem: Stem
    )


    @Serializable
    data class FlowBranches(
      val scales: Set<FlowScale>,
      val branch: Branch
    )

    /**
     * 五行生剋
     * DTO for [FlowPattern.Affecting]
     * transformed by [FlowDtoTransformer.toAffectingDtos]
     */
    @Serializable
    data class AffectingDto(
      override val description: String,
      val natalStems: NatalStems,
      val reacting: Reacting,
      val flowScales: Set<FlowScale>
    ) : EwFlow() {
      override val hourRelated: Boolean = natalStems.scales.contains(Scale.HOUR)
    }

    /**
     * 天干相合
     * DTO for [FlowPattern.StemCombined]
     * transformed by [FlowDtoTransformer.toStemCombinedDtos]
     */
    @Serializable
    data class StemCombinedDto(
      override val description: String,
      val natalStems: NatalStems,
      val flowStems: FlowStems,
      val combined: FiveElement
    ) : EwFlow() {
      override val hourRelated: Boolean = natalStems.scales.contains(Scale.HOUR)
    }

    /**
     * 地支相合
     * DTO for [FlowPattern.BranchCombined]
     * transformed by [FlowDtoTransformer.toBranchCombinedDtos]
     */
    @Serializable
    data class BranchCombinedDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.scales.contains(Scale.HOUR)
    }

    /**
     * 本命兩柱 與流運某干支 形成三合
     * DTO for [FlowPattern.TrilogyToFlow]
     * transformed by [FlowDtoTransformer.toTrilogyToFlowDtos]
     */
    @Serializable
    data class TrilogyToFlowDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>,
      val flowBranches: FlowBranches,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.any { it.scales.contains(Scale.HOUR) }
    }

    /**
     * 本命某柱 與流運兩柱 形成三合
     * DTO for [FlowPattern.ToFlowTrilogy]
     * transformed by [FlowDtoTransformer.toToFlowTrilogyDtos]
     */
    @Serializable
    data class ToFlowTrilogyDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: Set<FlowBranches>,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.scales.contains(Scale.HOUR)
    }

    /**
     * 地支正沖
     * DTO for [FlowPattern.BranchOpposition]
     * transformed by [FlowDtoTransformer.toBranchOppositionDtos]
     */
    @Serializable
    data class BranchOppositionDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.scales.contains(Scale.HOUR)
    }
  }
}


/** 占星事件 */
@Serializable
sealed class Astro : IAggregatedEvent {

  /** 交角 */
  @Serializable
  data class AspectEvent(
    override val description: String,
    val aspectData: AspectData,
    override val impact: Impact
  ) : Astro()

  /** 月亮空亡 */
  @Serializable
  data class MoonVoc(
    override val description: String,
    val voidCourseSpan: Misc.VoidCourseSpan
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 星體滯留 */
  @Serializable
  data class PlanetStationary(
    override val description: String,
    val stationary: Stationary,
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 當日星體逆行 */
  @Serializable
  data class PlanetRetrograde(
    override val description: String,
    val planet: Planet,
    val progress: Double
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 日食 or 月食 */
  @Serializable
  data class Eclipse(
    override val description: String,
    val eclipse: IEclipse,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 月相 */
  @Serializable
  data class LunarPhaseEvent(
    override val description: String,
    val phase: LunarPhase,
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

}


