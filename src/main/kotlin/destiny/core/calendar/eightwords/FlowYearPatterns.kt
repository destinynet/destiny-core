/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.FlowScale.LARGE
import destiny.core.FlowScale.YEAR
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.calendar.eightwords.FlowYearPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowYearPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowYearPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowYearPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowYearPatterns.trilogyToFlow
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
 * 大運、流年
 */
interface IFlowYearPatternFactory {
  fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern>
}

object FlowYearPatterns {

  val bothAffecting = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BothAffecting> {
      return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.mapNotNull { (scale, stem) ->
        val flowScales = setOf(LARGE, YEAR)
        when {
          stem.fiveElement.sameCount(flowLarge.stem, flowYear.stem) == 2      -> BothAffecting(scale, stem, SAME, flowScales)
          stem.fiveElement.producingCount(flowLarge.stem, flowYear.stem) == 2 -> BothAffecting(scale, stem, PRODUCING, flowScales)
          stem.fiveElement.producedCount(flowLarge.stem, flowYear.stem) == 2  -> BothAffecting(scale, stem, PRODUCED, flowScales)
          stem.fiveElement.dominatorCount(flowLarge.stem, flowYear.stem) == 2 -> BothAffecting(scale, stem, DOMINATING, flowScales)
          stem.fiveElement.beatenCount(flowLarge.stem, flowYear.stem) == 2    -> BothAffecting(scale, stem, BEATEN, flowScales)
          else                                                                -> null
        }
      }.toSet()
    }
  }

  val stemCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<StemCombined> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.stem }.flatMap { (scale, stem) ->
        buildSet {
          if (stem.combined.first == flowLarge.stem)
            add(StemCombined(scale, stem, LARGE))
          if (stem.combined.first == flowYear.stem)
            add(StemCombined(scale, stem, YEAR))
        }
      }.toSet()
    }
  }

  val branchCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
        buildSet {
          if (branch.combined == flowLarge.branch)
            add(BranchCombined(scale, branch, LARGE))
          if (branch.combined == flowYear.branch)
            add(BranchCombined(scale, branch, YEAR))
        }
      }.toSet()
    }
  }

  val trilogyToFlow = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<TrilogyToFlow> {
      return getScaleMap().entries.map { (scale, v) -> scale to v.branch }.let { pillars: List<Pair<Scale, Branch>> ->
        Sets.combinations(pillars.toSet(), 2).asSequence().filter { twoPillars: Set<Pair<Scale, Branch>> ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          p1.second.trilogy() == p2.second.trilogy()
        }.flatMap { twoPillars: Set<Pair<Scale, Branch>> ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          setOf(
            Triple(p1, p2, LARGE to flowLarge.branch),
            Triple(p1, p2, YEAR to flowYear.branch)
          )
        }.filter { (p1, p2, pFlow) ->
          trilogy(p1.second, p2.second, pFlow.second) != null
        }.map { (pair1, pair2, pairFlow) -> TrilogyToFlow(setOf(pair1, pair2), pairFlow) }
      }.toSet()
    }
  }

  val toFlowTrilogy = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<ToFlowTrilogy> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.mapNotNull { (scale, branch) ->
        if (trilogy(branch, flowLarge.branch, flowYear.branch) != null)
          ToFlowTrilogy(scale, branch, setOf(LARGE to flowLarge.branch, YEAR to flowYear.branch))
        else
          null
      }.toSet()
    }
  }

  val branchOpposition = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BranchOpposition> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
        buildSet {
          if (branch.opposite == flowLarge.branch)
            add(BranchOpposition(scale, branch, LARGE))
          if (branch.opposite == flowYear.branch)
            add(BranchOpposition(scale, branch, YEAR))
        }
      }.toSet()
    }
  }
}

fun IEightWords.getFlowYearPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern> {
  return setOf(
    bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition
  ).flatMap { factory: IFlowYearPatternFactory ->
    with(factory) {
      this@getFlowYearPatterns.getPatterns(flowLarge, flowYear)
    }
  }.toSet()
}
