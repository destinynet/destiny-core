/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.*
import destiny.core.calendar.eightwords.FlowYearPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowYearPatterns.stemCombined
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.FiveElement.Companion.beatenCount
import destiny.core.chinese.FiveElement.Companion.dominatorCount
import destiny.core.chinese.FiveElement.Companion.producedCount
import destiny.core.chinese.FiveElement.Companion.producingCount
import destiny.core.chinese.FiveElement.Companion.sameCount
import destiny.core.chinese.IStemBranch


interface IFlowYearPatternFactory {
  // TODO : 大運 , from personLargeFeature.getStemBranch
  fun IEightWords.getPatterns(fortuneLarge: IStemBranch, flowYear: IStemBranch): Set<EightWordsFlowYearPattern>
}

object FlowYearPatterns {

  val bothAffecting = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(fortuneLarge: IStemBranch, flowYear: IStemBranch): Set<BothAffecting> {
      return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.map { (scale , stem) ->
        if (stem.fiveElement.sameCount(fortuneLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, SAME)
        } else if (stem.fiveElement.producingCount(fortuneLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, PRODUCING)
        } else if (stem.fiveElement.producedCount(fortuneLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, PRODUCED)
        } else if (stem.fiveElement.dominatorCount(fortuneLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, DOMINATING)
        } else if (stem.fiveElement.beatenCount(fortuneLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, BEATEN)
        } else {
          null
        }
      }.filterNotNull().toSet()
    }
  }

  val stemCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(fortuneLarge: IStemBranch, flowYear: IStemBranch): Set<StemCombined> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.stem }.flatMap { (scale, stem) ->
        buildSet {
          if(stem.combined.first == fortuneLarge.stem)
            add(StemCombined(scale , stem , FlowScale.LARGE))
          if (stem.combined.first == flowYear.stem)
            add(StemCombined(scale, stem, FlowScale.YEAR))
        }
      }.toSet()
    }
  }

  val branchOpposition = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(fortuneLarge: IStemBranch, flowYear: IStemBranch): Set<BranchOpposition> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale , branch) ->
        buildSet {
          if(branch.opposite == fortuneLarge.branch)
            add(BranchOpposition(scale, branch, FlowScale.LARGE))
          if (branch.opposite == flowYear.branch)
            add(BranchOpposition(scale, branch, FlowScale.YEAR))
        }
      }.toSet()
    }
  }
}

fun IEightWords.getFlowYearPatterns(fortuneLarge: IStemBranch, flowYear: IStemBranch): Set<EightWordsFlowYearPattern> {
  return setOf(
    bothAffecting, stemCombined,
    branchOpposition
  ).flatMap { factory: IFlowYearPatternFactory ->
    with(factory) {
      this@getFlowYearPatterns.getPatterns(fortuneLarge, flowYear)
    }
  }.toSet()
}
