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
import destiny.core.chinese.eightwords.FlowTranslator
import destiny.core.chinese.eightwords.FlowTranslator.translateAffecting
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.FlowTranslator.translateStemCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateToFlowTrilogy
import destiny.core.chinese.eightwords.FlowTranslator.translateTrilogyToFlow
import destiny.core.chinese.eightwords.IdentityTranslator
import destiny.core.chinese.eightwords.IdentityTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.IdentityTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.IdentityTranslator.translateStemCombined
import destiny.core.chinese.eightwords.IdentityTranslator.translateStemRooted
import destiny.core.chinese.eightwords.IdentityTranslator.translateTrilogy
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


  class EwIdentity {

    /**
     * 本命天干合化
     * DTO for [IdentityPattern.StemCombined]
     * translated by [IdentityTranslator.translateStemCombined]
     * */
    @Serializable
    data class StemCombinedDto(
      val description : String,
      val natalStems: Set<NatalStems>,
      val combined: FiveElement
    )

    /**
     * 本命地支六合
     * DTO for [IdentityPattern.BranchCombined]
     * translated by [IdentityTranslator.translateBranchCombined]
     */
    @Serializable
    data class BranchCombinedDto(
      val description : String,
      val natalBranches: Set<NatalBranches>
    )

    /**
     * 本命地支三合
     * DTO for [IdentityPattern.Trilogy]
     * translated by [IdentityTranslator.translateTrilogy]
     *
     */
    @Serializable
    data class TrilogyDto(
      val description : String,
      val natalBranches: Set<NatalBranches>,
      val trilogy: FiveElement
    )

    /**
     * 本命地支正沖
     * DTO for [IdentityPattern.BranchOpposition]
     * translated by [IdentityTranslator.translateBranchOpposition]
     */
    @Serializable
    data class BranchOppositionDto(
      val description : String,
      val natalBranches: Set<NatalBranches>
    )

    /**
     * 本命天干通根
     * DTO for [IdentityPattern.StemRooted]
     * translated by [IdentityTranslator.translateStemRooted]
     */
    @Serializable
    data class StemRootedDto(
      val description : String,
      val natalStems: Set<NatalStems>,
      val natalBranches: Set<NatalBranches>
    )
  }

  class EwFlow {

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
     * translated by [FlowTranslator.translateAffecting]
     */
    @Serializable
    data class AffectingDto(
      val description: String,
      val natalStems: NatalStems,
      val reacting: Reacting,
      val flowScales: Set<FlowScale>
    )

    /**
     * 天干相合
     * DTO for [FlowPattern.StemCombined]
     * translated by [FlowTranslator.translateStemCombined]
     */
    @Serializable
    data class StemCombinedDto(
      val description: String,
      val natalStems: NatalStems,
      val flowStems: FlowStems,
      val combined: FiveElement
    )

    /**
     * 地支相合
     * DTO for [FlowPattern.BranchCombined]
     * translated by [FlowTranslator.translateBranchCombined]
     */
    @Serializable
    data class BranchCombinedDto(
      val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    )

    /**
     * 本命兩柱 與流運某干支 形成三合
     * DTO for [FlowPattern.TrilogyToFlow]
     * translated by [FlowTranslator.translateTrilogyToFlow]
     */
    @Serializable
    data class TrilogyToFlowDto(
      val description: String,
      val natalBranches: Set<NatalBranches>,
      val flowBranches: FlowBranches,
    )

    /**
     * 本命某柱 與流運兩柱 形成三合
     * DTO for [FlowPattern.ToFlowTrilogy]
     * translated by [FlowTranslator.translateToFlowTrilogy]
     */
    @Serializable
    data class ToFlowTrilogyDto(
      val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: Set<FlowBranches>,
    )

    /**
     * 地支正沖
     * DTO for [FlowPattern.BranchOpposition]
     * translated by [FlowTranslator.translateBranchOpposition]
     */
    @Serializable
    data class BranchOppositionDto(
      val description: String,
      val natalBranches: NatalBranches,
      val flowBranches: FlowBranches,
    )
  }

}
