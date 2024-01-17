/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.BranchOpposite
import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.StemCombined
import destiny.core.chinese.IStemBranch


interface IFlowYearPatternFactory {
  fun IEightWords.getPatterns(flowYear: IStemBranch): Set<EightWordsFlowYearPattern>
}

object FlowYearPatterns {

  val stemCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch): Set<StemCombined> {
      val scales = getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.stem }.filter { (_, stem) ->
        stem.combined.first == flowYear.stem
      }.map { (scale, _) -> scale }
        .toSet()
      return scales.takeIf { it.isNotEmpty() }?.let {
        setOf(StemCombined(it, flowYear.stem.combined.first))
      } ?: emptySet()
    }
  }

  val branchOpposition = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch): Set<BranchOpposite> {
      val scales = getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.filter { (_, branch) ->
        branch.opposite == flowYear.branch
      }.map { (scale, _) -> scale }
        .toSet()
      return scales.takeIf { it.isNotEmpty() }?.let {
        setOf(BranchOpposite(it, flowYear.branch.opposite))
      } ?: emptySet()
    }
  }
}

fun IEightWords.getPatterns(flowYear: IStemBranch): Set<EightWordsFlowYearPattern> {
  return setOf(
    FlowYearPatterns.stemCombined
  ).flatMap { factory: IFlowYearPatternFactory ->
    with(factory) {
      this@getPatterns.getPatterns(flowYear)
    }
  }.toSet()
}
