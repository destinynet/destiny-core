/**
 * Created by smallufo on 2025-05-26.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.FlowScale.DAY
import destiny.core.FlowScale.HOUR
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.chinese.FiveElement.Companion.beatenCount
import destiny.core.chinese.FiveElement.Companion.dominatorCount
import destiny.core.chinese.FiveElement.Companion.producedCount
import destiny.core.chinese.FiveElement.Companion.producingCount
import destiny.core.chinese.FiveElement.Companion.sameCount
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.trilogy

/**
 * 流日、流時
 */
interface IFlowHourPatternFactory {
  fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<FlowPattern>
}

object FlowHourPatterns {

  val bothAffecting = object : IFlowHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<BothAffecting> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.stem }.mapNotNull { (scale, stem) ->
        val flowScales = setOf(DAY, HOUR)
        when {
          stem.fiveElement.sameCount(flowDay.stem, flowHour.stem) == 2      -> BothAffecting(scale, stem, Reacting.SAME, flowScales)
          stem.fiveElement.producingCount(flowDay.stem, flowHour.stem) == 2 -> BothAffecting(scale, stem, Reacting.PRODUCING, flowScales)
          stem.fiveElement.producedCount(flowDay.stem, flowHour.stem) == 2  -> BothAffecting(scale, stem, Reacting.PRODUCED, flowScales)
          stem.fiveElement.dominatorCount(flowDay.stem, flowHour.stem) == 2 -> BothAffecting(scale, stem, Reacting.DOMINATING, flowScales)
          stem.fiveElement.beatenCount(flowDay.stem, flowHour.stem) == 2    -> BothAffecting(scale, stem, Reacting.BEATEN, flowScales)
          else                                                              -> null
        }
      }.toSet()
    }
  }

  val stemCombined = object : IFlowHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<StemCombined> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.stem }.flatMap { (scale, stem) ->
        buildSet {
          if (stem.combined.first == flowDay.stem)
            add(StemCombined(scale, stem, DAY))
          if (stem.combined.first == flowHour.stem)
            add(StemCombined(scale, stem, HOUR))
        }
      }.toSet()
    }
  }

  val branchCombined = object : IFlowHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<BranchCombined> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
        buildSet {
          if (branch.combined == flowDay.branch)
            add(BranchCombined(scale, branch, DAY))
          if (branch.combined == flowHour.branch)
            add(BranchCombined(scale, branch, HOUR))
        }
      }.toSet()
    }
  }

  val trilogyToFlow = object : IFlowHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<TrilogyToFlow> {
      return getScaleMap().entries.map { (scale, v) -> scale to v.branch }.let { pillars ->
        Sets.combinations(pillars.toSet(), 2).asSequence().filter { twoPillars ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          p1.second.trilogy() == p2.second.trilogy()
        }.flatMap { twoPillars ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          setOf(
            Triple(p1, p2, DAY to flowDay.branch),
            Triple(p1, p2, HOUR to flowHour.branch)
          )
        }.filter { (p1, p2, pFlow) ->
          trilogy(p1.second, p2.second, pFlow.second) != null
        }.map { (pair1, pair2, pairFlow) ->
          TrilogyToFlow(setOf(pair1, pair2), pairFlow)
        }
      }.toSet()
    }
  }

  val toFlowTrilogy = object : IFlowHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<ToFlowTrilogy> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.mapNotNull { (scale, branch) ->
        if (trilogy(branch, flowDay.branch, flowHour.branch) != null)
          ToFlowTrilogy(scale, branch, setOf(DAY to flowDay.branch, HOUR to flowHour.branch))
        else
          null
      }.toSet()
    }
  }

  val branchOpposition = object : IFlowHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<FlowPattern> {
      return getScaleMap().entries.asSequence().map { (scale, v) -> scale to v.branch }.flatMap { (scale, branch) ->
        buildSet {
          if (branch.opposite == flowDay.branch)
            add(BranchOpposition(scale, branch, DAY))
          if (branch.opposite == flowHour.branch)
            add(BranchOpposition(scale, branch, HOUR))
        }
      }.toSet()
    }
  }
}

