/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.electional

import destiny.core.Scale
import destiny.core.calendar.eightwords.IdentityPattern
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem
import destiny.core.chinese.eightwords.IdentityTranslator
import destiny.core.chinese.eightwords.IdentityTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.IdentityTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.IdentityTranslator.translateStemCombined
import destiny.core.chinese.eightwords.IdentityTranslator.translateStemRooted
import destiny.core.chinese.eightwords.IdentityTranslator.translateTrilogy
import kotlinx.serialization.Serializable

class Dtos {

  class EwIdentity {

    @Serializable
    data class NatalStems(
      val scales : Set<Scale>,
      val stem: Stem
    )

    @Serializable
    data class NatalBranches(
      val scales : Set<Scale>,
      val branch: Branch
    )

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

}
