/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.Scale
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

sealed interface IEventDto {
  val description: String
}


class Dtos {

  @Serializable
  sealed class EwEvent {

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
    sealed class EwIdentity : IEventDto {

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
    sealed class EwFlow : IEventDto {

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



  @Serializable
  sealed class Astro : IEventDto {

//    @Serializable
//    data class AspectEvent(
//      override val description: String,
//      val aspectData: AspectData,
//      val impact: Impact
//    ) : Astro()

  }

}
