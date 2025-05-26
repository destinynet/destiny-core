/**
 * Created by smallufo on 2024-01-15.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowMonthPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowMonthPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowMonthPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowMonthPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowMonthPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowMonthPatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement.Companion.beatenCount
import destiny.core.chinese.FiveElement.Companion.dominatorCount
import destiny.core.chinese.FiveElement.Companion.producedCount
import destiny.core.chinese.FiveElement.Companion.producingCount
import destiny.core.chinese.FiveElement.Companion.sameCount
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.trilogy

/**
 * 流年、流月
 */
interface IFlowMonthPatternFactory {
  fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<FlowPattern>
}

object FlowMonthPatterns {

  val bothAffecting = object : IFlowMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BothAffecting> {
      return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.mapNotNull { (scale, stem) ->
        val flowScales = setOf(FlowScale.YEAR, FlowScale.MONTH)
        when {
          stem.fiveElement.sameCount(flowYear.stem, flowMonth.stem) == 2      -> BothAffecting(scale, stem, SAME, flowScales)
          stem.fiveElement.producingCount(flowYear.stem, flowMonth.stem) == 2 -> BothAffecting(scale, stem, PRODUCING, flowScales)
          stem.fiveElement.producedCount(flowYear.stem, flowMonth.stem) == 2  -> BothAffecting(scale, stem, PRODUCED, flowScales)
          stem.fiveElement.dominatorCount(flowYear.stem, flowMonth.stem) == 2 -> BothAffecting(scale, stem, DOMINATING, flowScales)
          stem.fiveElement.beatenCount(flowYear.stem, flowMonth.stem) == 2    -> BothAffecting(scale, stem, BEATEN, flowScales)
          else                                                                -> null
        }
      }.toSet()
    }
  }

  val stemCombined = object : IFlowMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<StemCombined> {
      return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.flatMap { (scale, stem) ->
        buildSet {
          if (stem.combined.first == flowYear.stem)
            add(StemCombined(scale, stem, FlowScale.YEAR))
          if (stem.combined.first == flowMonth.stem)
            add(StemCombined(scale, stem, FlowScale.MONTH))
        }
      }.toSet()
    }
  }

  val branchCombined = object : IFlowMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BranchCombined> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
        buildSet {
          if (branch.combined == flowYear.branch)
            add(BranchCombined(scale, branch, FlowScale.YEAR))
          if (branch.combined == flowMonth.branch)
            add(BranchCombined(scale, branch, FlowScale.MONTH))
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
            Triple(p1, p2, FlowScale.YEAR to flowYear.branch),
            Triple(p1, p2, FlowScale.MONTH to flowMonth.branch)
          )
        }.filter { (p1, p2, pFlow) ->
          trilogy(p1.second, p2.second, pFlow.second) != null
        }.map { (pair1: Pair<Scale, Branch>, pair2, pairFlow) -> TrilogyToFlow(setOf(pair1, pair2), pairFlow) }
      }.toSet()
    }
  }

  val toFlowTrilogy = object : IFlowMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<ToFlowTrilogy> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.mapNotNull { (scale, branch) ->
        if (trilogy(branch, flowYear.branch, flowMonth.branch) != null)
          ToFlowTrilogy(scale, branch, setOf(FlowScale.YEAR to flowYear.branch, FlowScale.MONTH to flowMonth.branch))
        else
          null
      }.toSet()
    }
  }

  val branchOpposition = object : IFlowMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BranchOpposition> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
        buildSet {
          if (branch.opposite == flowYear.branch)
            add(BranchOpposition(scale, branch, FlowScale.YEAR))
          if (branch.opposite == flowMonth.branch)
            add(BranchOpposition(scale, branch, FlowScale.MONTH))
        }
      }.toSet()
    }
  }
}


fun IEightWords.getFlowMonthPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<FlowPattern> {
  return setOf(
    bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition
  ).flatMap { factory: IFlowMonthPatternFactory ->
    with(factory) {
      this@getFlowMonthPatterns.getPatterns(flowYear, flowMonth)
    }
  }.toSet()
}

