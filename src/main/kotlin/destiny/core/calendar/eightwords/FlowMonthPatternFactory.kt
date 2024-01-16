/**
 * Created by smallufo on 2024-01-15.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.Scale
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement.Companion.beatenCount
import destiny.core.chinese.FiveElement.Companion.dominatorCount
import destiny.core.chinese.FiveElement.Companion.producedCount
import destiny.core.chinese.FiveElement.Companion.producingCount
import destiny.core.chinese.FiveElement.Companion.sameCount
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.trilogy


interface IFlowMonthPatternFactory {
  fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<IEightWordsFlowMonthPattern>
}

val bothAffecting = object : IFlowMonthPatternFactory {
  override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BothAffecting> {
    return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.map { (scale, stem) ->
      if (stem.fiveElement.sameCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Same(scale, stem)
      } else if (stem.fiveElement.producingCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Producing(scale, stem)
      } else if (stem.fiveElement.producedCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Produced(scale, stem)
      } else if (stem.fiveElement.dominatorCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Dominating(scale, stem)
      } else if (stem.fiveElement.beatenCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Beaten(scale, stem)
      } else {
        null
      }
    }.filterNotNull().toSet()
  }
}

val stemCombined = object : IFlowMonthPatternFactory {
  override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<StemCombined> {
    return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.flatMap { (scale, stem) ->
      buildSet {
        if (stem.combined.first == flowYear.stem)
          add(StemCombined(scale, stem, Scale.YEAR))
        if (stem.combined.first == flowMonth.stem)
          add(StemCombined(scale, stem, Scale.MONTH))
      }
    }.toSet()
  }
}

val branchCombined = object : IFlowMonthPatternFactory {
  override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BranchCombined> {
    return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
      buildSet {
        if (branch.combined == flowYear.branch)
          add(BranchCombined(scale , branch , Scale.YEAR))
        if (branch.combined == flowMonth.branch)
          add(BranchCombined(scale , branch, Scale.MONTH))
      }
    }.toSet()
  }
}

val trilogyToFlow = object : IFlowMonthPatternFactory {
  override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<TrilogyToFlow> {
    return getScaleMap().entries.map { (scale, v) -> scale to v.branch }.let { pillars ->
      Sets.combinations(pillars.toSet(), 2).asSequence().filter { twoPillars: Set<Pair<Scale, Branch>> ->
        val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
        p1.second.trilogy() == p2.second.trilogy()
      }.flatMap { twoPillars: Set<Pair<Scale, Branch>> ->
        val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
        setOf(
          Triple(p1, p2, Scale.YEAR to flowYear.branch),
          Triple(p1, p2, Scale.MONTH to flowMonth.branch)
        )
      }.filter { (p1, p2, pFlow) ->
        trilogy(p1.second, p2.second, pFlow.second) != null
      }.map { (pair1: Pair<Scale, Branch>, pair2, pairFlow) -> TrilogyToFlow(setOf(pair1, pair2), pairFlow) }
    }.toSet()
  }
}

val toFlowTrilogy = object : IFlowMonthPatternFactory {
  override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<ToFlowTrilogy> {
    return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.mapNotNull { (scale , branch) ->
      if (trilogy(branch, flowYear.branch, flowMonth.branch) != null)
        ToFlowTrilogy(scale, branch, Scale.YEAR to flowYear.branch, Scale.MONTH to flowMonth.branch)
      else
        null
    }.toSet()
  }

}

fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<IEightWordsFlowMonthPattern> {
  return setOf(
    bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy
  ).flatMap { factory: IFlowMonthPatternFactory ->
    with(factory) {
      this@getPatterns.getPatterns(flowYear, flowMonth)
    }
  }.toSet()
}

