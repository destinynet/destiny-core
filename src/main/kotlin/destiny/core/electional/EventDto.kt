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
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IZodiacDegreeSerializer
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
enum class Span {
  DAY,
  HOURS, // 數小時
  INSTANT
}

@Serializable
sealed interface IAggregatedEvent {
  val description: String
  val impact: Impact
}

@Serializable(with = IEventDtoSerializer::class)
sealed interface IEventDto : Comparable<IEventDto> {
  val event: IAggregatedEvent
  val begin: LocalDateTime
  val end: LocalDateTime?

  val span: Span

  override fun compareTo(other: IEventDto): Int {
    return begin.compareTo(other.begin)
  }
}

object IEventDtoSerializer : KSerializer<IEventDto> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IEventDto") {
    element("event", IAggregatedEvent.serializer().descriptor)
    element("begin", LocalDateTimeSerializer.descriptor)
    element("end", LocalDateTimeSerializer.descriptor, isOptional = true)
    element("span", Span.serializer().descriptor)
  }

  override fun serialize(encoder: Encoder, value: IEventDto) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, IAggregatedEvent.serializer(), value.event)
      encodeSerializableElement(descriptor, 1, LocalDateTimeSerializer, value.begin)
      value.end?.also { encodeSerializableElement(descriptor, 2, LocalDateTimeSerializer, it) }
      encodeSerializableElement(descriptor, 3, Span.serializer(), value.span)
    }
  }

  override fun deserialize(decoder: Decoder): IEventDto {
    throw UnsupportedOperationException("Deserialization not supported for IEventDto")
  }
}

@Serializable
data class EwEventDto(
  override val event: Ew,
  val outer: IEightWords,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val begin: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val end : LocalDateTime? = null,
  override val span: Span
) : IEventDto

@Serializable
data class AstroEventDto(
  override val event: Astro,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val begin: LocalDateTime,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val end : LocalDateTime? = null,
  override val span: Span
) : IEventDto

@Serializable
data class Daily(
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  val allDayEvents: List<IEventDto>,
  val hourEvents: Map<@Serializable(with = LocalDateTimeSerializer::class) LocalDateTime, List<IEventDto>>
) : Comparable<Daily> {
  override fun compareTo(other: Daily): Int {
    return localDate.compareTo(other.localDate)
  }
}

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
    ) : EwIdentity()

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
    ) : EwIdentity()

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
    ) : EwIdentity()

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
    ) : EwIdentity()

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
    ) : EwIdentity()

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
    ) : EwIdentity()

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
    @SerialName("Flow.Affecting")
    data class AffectingDto(
      override val description: String,
      val natalStems: NatalStems,
      val reacting: Reacting,
      val flowScales: Set<FlowScale>
    ) : EwFlow() {
      override val hourRelated: Boolean = natalStems.pillars.contains(Scale.HOUR)
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
    }
  }
}


/** 占星事件 */
@Serializable
sealed class Astro : IAggregatedEvent {

  /** 交角 */
  @Serializable
  @SerialName("Astro.AspectEvent")
  data class AspectEvent(
    override val description: String,
    val aspectData: AspectData,
    override val impact: Impact
  ) : Astro()

  /** 月亮空亡 */
  @Serializable
  @SerialName("Astro.MoonVoc")
  data class MoonVoc(
    override val description: String,
    val voidCourseSpan: Misc.VoidCourseSpan
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 星體滯留 */
  @Serializable
  @SerialName("Astro.PlanetStationary")
  data class PlanetStationary(
    override val description: String,
    val stationary: Stationary,
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 當日星體逆行 */
  @Serializable
  @SerialName("Astro.PlanetRetrograde")
  data class PlanetRetrograde(
    override val description: String,
    val planet: Planet,
    @Serializable(with = DoubleTwoDecimalSerializer::class)
    val progress: Double
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 日食 or 月食 */
  @Serializable
  @SerialName("Astro.Eclipse")
  data class Eclipse(
    override val description: String,
    val eclipse: IEclipse,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

  /** 月相 */
  @Serializable
  @SerialName("Astro.LunarPhaseEvent")
  data class LunarPhaseEvent(
    override val description: String,
    val phase: LunarPhase,
    @Serializable(with = IZodiacDegreeSerializer::class)
    val zodiacDegree: IZodiacDegree,
    val transitToNatalAspects: List<SynastryAspect>
  ) : Astro() {
    override val impact: Impact = Impact.GLOBAL
  }

}


