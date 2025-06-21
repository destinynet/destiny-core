/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowPattern
import destiny.core.calendar.eightwords.IdentityPattern
import destiny.core.calendar.eightwords.Reacting
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem
import destiny.core.chinese.eightwords.FlowDtoTransformer
import destiny.core.chinese.eightwords.FlowDtoTransformer.translateAffecting
import destiny.core.chinese.eightwords.FlowDtoTransformer.translateBranchCombined
import destiny.core.chinese.eightwords.FlowDtoTransformer.translateBranchOpposition
import destiny.core.chinese.eightwords.FlowDtoTransformer.translateStemCombined
import destiny.core.chinese.eightwords.FlowDtoTransformer.translateToFlowTrilogy
import destiny.core.chinese.eightwords.FlowDtoTransformer.translateTrilogyToFlow
import destiny.core.chinese.eightwords.IdentityDtoTransformer
import destiny.core.chinese.eightwords.IdentityDtoTransformer.translateBranchCombined
import destiny.core.chinese.eightwords.IdentityDtoTransformer.translateBranchOpposition
import destiny.core.chinese.eightwords.IdentityDtoTransformer.translateStemCombined
import destiny.core.chinese.eightwords.IdentityDtoTransformer.translateStemRooted
import destiny.core.chinese.eightwords.IdentityDtoTransformer.translateTrilogy
import kotlinx.serialization.Serializable

class Dtos {

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
     * translated by [IdentityDtoTransformer.translateStemCombined]
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
     * translated by [IdentityDtoTransformer.translateBranchCombined]
     */
    @Serializable
    data class BranchCombinedDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity()

    /**
     * 本命地支三合
     * DTO for [IdentityPattern.Trilogy]
     * translated by [IdentityDtoTransformer.translateTrilogy]
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
     * translated by [IdentityDtoTransformer.translateBranchOpposition]
     */
    @Serializable
    data class BranchOppositionDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity()

    /**
     * 本命天干通根
     * DTO for [IdentityPattern.StemRooted]
     * translated by [IdentityDtoTransformer.translateStemRooted]
     */
    @Serializable
    data class StemRootedDto(
      override val description: String,
      val natalStems: Set<NatalStems>,
      val natalBranches: Set<NatalBranches>
    ) : EwIdentity()
  }

  @Serializable
  sealed class EwFlow : IEventDto {

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
     * translated by [FlowDtoTransformer.translateAffecting]
     */
    @Serializable
    data class AffectingDto(
      override val description: String,
      val natalStems: NatalStems,
      val reacting: Reacting,
      val flowScales: Set<FlowScale>
    ) : EwFlow()

    /**
     * 天干相合
     * DTO for [FlowPattern.StemCombined]
     * translated by [FlowDtoTransformer.translateStemCombined]
     */
    @Serializable
    data class StemCombinedDto(
      override val description: String,
      val natalStems: NatalStems,
      val flowStems: FlowStems,
      val combined: FiveElement
    ) : EwFlow()

    /**
     * 地支相合
     * DTO for [FlowPattern.BranchCombined]
     * translated by [FlowDtoTransformer.translateBranchCombined]
     */
    @Serializable
    data class BranchCombinedDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    ) : EwFlow()

    /**
     * 本命兩柱 與流運某干支 形成三合
     * DTO for [FlowPattern.TrilogyToFlow]
     * translated by [FlowDtoTransformer.translateTrilogyToFlow]
     */
    @Serializable
    data class TrilogyToFlowDto(
      override val description: String,
      val natalBranches: Set<NatalBranches>,
      val flowBranches: FlowBranches,
    ) : EwFlow()

    /**
     * 本命某柱 與流運兩柱 形成三合
     * DTO for [FlowPattern.ToFlowTrilogy]
     * translated by [FlowDtoTransformer.translateToFlowTrilogy]
     */
    @Serializable
    data class ToFlowTrilogyDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: Set<FlowBranches>,
    ) : EwFlow()

    /**
     * 地支正沖
     * DTO for [FlowPattern.BranchOpposition]
     * translated by [FlowDtoTransformer.translateBranchOpposition]
     */
    @Serializable
    data class BranchOppositionDto(
      override val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    ) : EwFlow()
  }

}
