/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.*
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


interface IFlowYearPatternFactory {
  fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<EightWordsFlowYearPattern>
}

object FlowYearPatterns {

  val bothAffecting = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BothAffecting> {
      return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.map { (scale , stem) ->
        if (stem.fiveElement.sameCount(flowLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, SAME)
        } else if (stem.fiveElement.producingCount(flowLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, PRODUCING)
        } else if (stem.fiveElement.producedCount(flowLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, PRODUCED)
        } else if (stem.fiveElement.dominatorCount(flowLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, DOMINATING)
        } else if (stem.fiveElement.beatenCount(flowLarge.stem, flowYear.stem) == 2) {
          BothAffecting(scale, stem, BEATEN)
        } else {
          null
        }
      }.filterNotNull().toSet()
    }
  }

  val stemCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<StemCombined> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.stem }.flatMap { (scale, stem) ->
        buildSet {
          if(stem.combined.first == flowLarge.stem)
            add(StemCombined(scale , stem , FlowScale.LARGE))
          if (stem.combined.first == flowYear.stem)
            add(StemCombined(scale, stem, FlowScale.YEAR))
        }
      }.toSet()
    }
  }

  val branchCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<EightWordsFlowYearPattern> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale , branch) ->
        buildSet {
          if (branch.combined == flowLarge.branch)
            add(BranchCombined(scale, branch, FlowScale.LARGE))
          if (branch.combined == flowYear.branch)
            add(BranchCombined(scale, branch, FlowScale.YEAR))
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
            Triple(p1, p2, FlowScale.LARGE to flowLarge.branch),
            Triple(p1,p2 , FlowScale.YEAR to flowYear.branch)
          )
        }.filter { (p1, p2, pFlow) ->
          trilogy(p1.second, p2.second, pFlow.second) != null
        }.map { (pair1 , pair2 , pairFlow) -> TrilogyToFlow(setOf(pair1 , pair2) , pairFlow) }
      }.toSet()
    }
  }

  val toFlowTrilogy = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<ToFlowTrilogy> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.mapNotNull { (scale, branch) ->
        if (trilogy(branch, flowLarge.branch, flowYear.branch) != null)
          ToFlowTrilogy(scale, branch, setOf(FlowScale.LARGE to flowLarge.branch, FlowScale.YEAR to flowYear.branch))
        else
          null
      }.toSet()
    }
  }

  val branchOpposition = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BranchOpposition> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale , branch) ->
        buildSet {
          if(branch.opposite == flowLarge.branch)
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
    bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition
  ).flatMap { factory: IFlowYearPatternFactory ->
    with(factory) {
      this@getFlowYearPatterns.getPatterns(fortuneLarge, flowYear)
    }
  }.toSet()
}
