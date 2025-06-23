/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipse
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
import destiny.tools.serializers.IZodiacDegreeSerializer
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

enum class Span {
  DAY,
  HOURS, // 數小時
  INSTANT
}

@Serializable
sealed interface IAggregatedEvent {
  val description: String
  val impact: Impact
  val span: Span
}

@Serializable
sealed interface IEventDto : Comparable<IEventDto> {
  val event: IAggregatedEvent
  val begin: LocalDateTime
  val end: LocalDateTime?
  val impact: Impact

  override fun compareTo(other: IEventDto): Int {
    return begin.compareTo(other.begin)
  }
}

@Serializable
data class EwEventDto(
  override val event: Ew,
  val outer: IEightWords,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val begin: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val end : LocalDateTime? = null
) : IEventDto {
  override val impact: Impact = event.impact
}

@Serializable
data class AstroEventDto(
  override val event: Astro,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val begin: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val end : LocalDateTime? = null
) : IEventDto {
  override val impact: Impact = event.impact
}


@Serializable
data class Daily(
  @Serializable(with = LocalDateSerializer::class)
  val localDate : LocalDate,
  val allDayEvents : List<IEventDto>,
  val hourEvents : List<IEventDto>
)

/** 八字事件 */
@Serializable
sealed class Ew : IAggregatedEvent {

  @Serializable
  data class NatalStems(
    val pillars: Set<Scale>,
    val stem: Stem
  )

  @Serializable
  data class NatalBranches(
    val pillars: Set<Scale>,
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
    @SerialName("Identity.StemCombined")
    data class StemCombinedDto(
      override val description: String,
      val natalStems: Set<NatalStems>,
      val combined: FiveElement
    ) : EwIdentity() {
      override val span: Span = natalStems.findSpanByStems()
    }

    /**
     * 本命地支六合
     * DTO for [IdentityPattern.BranchCombined]
     * transformed by [IdentityDtoTransformer.toBranchCombinedDtos]
     */
    @Serializable
    @SerialName("Identity.BranchCombined")
    data class BranchCombinedDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity() {
      override val span: Span = natalBranches.findSpanByBranches()
    }

    /**
     * 本命地支三合
     * DTO for [IdentityPattern.Trilogy]
     * transformed by [IdentityDtoTransformer.toTrilogyDtos]
     *
     */
    @Serializable
    @SerialName("Identity.Trilogy")
    data class TrilogyDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>,
      val trilogy: FiveElement
    ) : EwIdentity() {
      override val span: Span = natalBranches.findSpanByBranches()
    }

    /**
     * 本命地支正沖
     * DTO for [IdentityPattern.BranchOpposition]
     * transformed by [IdentityDtoTransformer.toBranchOppositionDtos]
     */
    @Serializable
    @SerialName("Identity.BranchOpposition")
    data class BranchOppositionDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity() {
      override val span: Span = natalBranches.findSpanByBranches()
    }

    /**
     * 本命天干通根
     * DTO for [IdentityPattern.StemRooted]
     * transformed by [IdentityDtoTransformer.toStemRootedDtos]
     */
    @Serializable
    @SerialName("Identity.StemRooted")
    data class StemRootedDto(
      override val description: String,
      val natalStems: Set<NatalStems>,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity() {
      override val span: Span
        get() {
          return setOf(
            natalStems.maxBy { it.pillars.max() }.pillars.max(),
            natalBranches.maxBy { it.pillars.max() }.pillars.max()
          ).max().toSpan()
        }
    }

    /**
     * 吉祥
     * DTO for [IdentityPattern.AuspiciousPattern]
     * transformed by [IdentityDtoTransformer.toAuspiciousDto]
     */
    @Serializable
    @SerialName("Identity.Auspicious")
    data class AuspiciousDto(
      override val description: String,
      val pillars: Map<Scale, Set<Auspicious>>
    ) : EwIdentity() {
      override val span: Span = pillars.filter { it.value.isNotEmpty() }.keys.max().toSpan()
    }

    /**
     * 不祥
     * DTO for [IdentityPattern.InauspiciousPattern]
     * transformed by [IdentityDtoTransformer.toInauspiciousDto]
     */
    @Serializable
    @SerialName("Identity.Inauspicious")
    data class InauspiciousDto(
      override val description: String,
      val pillars: Map<Scale, Set<Inauspicious>>
    ) : EwIdentity() {
      override val span: Span = pillars.filter { it.value.isNotEmpty() }.keys.max().toSpan()
    }
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
    @SerialName("Flow.Affecting")
    data class AffectingDto(
      override val description: String,
      val natalStems: NatalStems,
      val reacting: Reacting,
      val flowScales: Set<FlowScale>
    ) : EwFlow() {
      override val hourRelated: Boolean = natalStems.pillars.contains(Scale.HOUR)
      override val span: Span = flowScales.max().let {
        when (it) {
          FlowScale.DAY  -> Span.DAY
          FlowScale.HOUR -> Span.HOURS
          else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
        }
      }
    }

    /**
     * 天干相合
     * DTO for [FlowPattern.StemCombined]
     * transformed by [FlowDtoTransformer.toStemCombinedDtos]
     */
    @Serializable
    @SerialName("Flow.StemCombined")
    data class StemCombinedDto(
      override val description: String,
      val natalStems: NatalStems,
      val flowStems: FlowStems,
      val combined: FiveElement
    ) : EwFlow() {
      override val hourRelated: Boolean = natalStems.pillars.contains(Scale.HOUR)
      override val span: Span = flowStems.scales.max().let {
        when (it) {
          FlowScale.DAY  -> Span.DAY
          FlowScale.HOUR -> Span.HOURS
          else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
        }
      }
    }

    /**
     * 地支相合
     * DTO for [FlowPattern.BranchCombined]
     * transformed by [FlowDtoTransformer.toBranchCombinedDtos]
     */
    @Serializable
    @SerialName("Flow.BranchCombined")
    data class BranchCombinedDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.pillars.contains(Scale.HOUR)
      override val span: Span = flowBranches.scales.max().let {
        when (it) {
          FlowScale.DAY  -> Span.DAY
          FlowScale.HOUR -> Span.HOURS
          else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
        }
      }
    }

    /**
     * 本命兩柱 與流運某干支 形成三合
     * DTO for [FlowPattern.TrilogyToFlow]
     * transformed by [FlowDtoTransformer.toTrilogyToFlowDtos]
     */
    @Serializable
    @SerialName("Flow.TrilogyToFlow")
    data class TrilogyToFlowDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>,
      val flowBranches: FlowBranches,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.any { it.pillars.contains(Scale.HOUR) }
      override val span: Span = flowBranches.scales.max().let {
        when (it) {
          FlowScale.DAY  -> Span.DAY
          FlowScale.HOUR -> Span.HOURS
          else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
        }
      }
    }

    /**
     * 本命某柱 與流運兩柱 形成三合
     * DTO for [FlowPattern.ToFlowTrilogy]
     * transformed by [FlowDtoTransformer.toToFlowTrilogyDtos]
     */
    @Serializable
    @SerialName("Flow.ToFlowTrilogy")
    data class ToFlowTrilogyDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: Set<FlowBranches>,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.pillars.contains(Scale.HOUR)
      override val span: Span = flowBranches.maxBy { it.scales.max() }.scales.max().let {
        when (it) {
          FlowScale.DAY  -> Span.DAY
          FlowScale.HOUR -> Span.HOURS
          else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
        }
      }
    }

    /**
     * 地支正沖
     * DTO for [FlowPattern.BranchOpposition]
     * transformed by [FlowDtoTransformer.toBranchOppositionDtos]
     */
    @Serializable
    @SerialName("Flow.BranchOpposition")
    data class BranchOppositionDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    ) : EwFlow() {
      override val hourRelated: Boolean = natalBranches.pillars.contains(Scale.HOUR)
      override val span: Span = flowBranches.scales.max().let {
        when (it) {
          FlowScale.DAY  -> Span.DAY
          FlowScale.HOUR -> Span.HOURS
          else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
        }
      }
    }
  }

  companion object {
    private const val NOT_SUPPORTED = "not supported"
    private const val IMPOSSIBLE_FLOW_SCALE = "impossible flowScale"

    fun Set<NatalStems>.findSpanByStems(): Span {
      return this.maxBy { it.pillars.max() }.pillars.max().toSpan()
    }

    fun Set<NatalBranches>.findSpanByBranches(): Span {
      return this.maxBy { it.pillars.max() }.pillars.max().toSpan()
    }

    fun Scale.toSpan(): Span {
      return when (this) {
        Scale.DAY               -> Span.DAY
        Scale.HOUR              -> Span.HOURS
        Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
      }
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
  ) : Astro() {
    override val span: Span = Span.INSTANT
  }

  /** 月亮空亡 */
  @Serializable
  data class MoonVoc(
    override val description: String,
    val voidCourseSpan: Misc.VoidCourseSpan
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
    override val span: Span = Span.HOURS
  }

  /** 星體滯留 */
  @Serializable
  data class PlanetStationary(
    override val description: String,
    val stationary: Stationary,
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
    override val span: Span = Span.INSTANT
  }

  /** 當日星體逆行 */
  @Serializable
  data class PlanetRetrograde(
    override val description: String,
    val planet: Planet,
    val progress: Double
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
    override val span: Span = Span.DAY
  }

  /** 日食 or 月食 */
  @Serializable
  data class Eclipse(
    override val description: String,
    val eclipse: IEclipse,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
    override val span: Span = Span.HOURS
  }

  /** 月相 */
  @Serializable
  data class LunarPhaseEvent(
    override val description: String,
    val phase: LunarPhase,
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
    override val span: Span = Span.INSTANT
  }

}


