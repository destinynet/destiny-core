/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowLargePatterns.branchCombined
import destiny.core.calendar.eightwords.FlowLargePatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowLargePatterns.stemCombined
import destiny.core.calendar.eightwords.FlowLargePatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.trilogy


interface IFlowLargePatternFactory {
  fun IEightWords.getPatterns(flowLarge: IStemBranch): Set<FlowPattern>
}

object FlowLargePatterns {

  val stemCombined = object : IFlowLargePatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch): Set<StemCombined> {
      return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }
        .filter { (_, stem) -> stem.combined.first == flowLarge.stem }
        .map { (scale, stem) -> StemCombined(scale, stem, FlowScale.LARGE) }
        .toSet()
    }
  }

  val branchCombined = object : IFlowLargePatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch): Set<BranchCombined> {
      return getScaleMap().entries.asSequence()
        .map { (scale, v) -> scale to v.branch }
        .filter { (_, branch) -> branch.combined == flowLarge.branch }
        .map { (scale, branch) -> BranchCombined(scale, branch, FlowScale.LARGE) }
        .toSet()
    }
  }

  val trilogyToFlow = object : IFlowLargePatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch): Set<TrilogyToFlow> {
      return getScaleMap().entries.map { (scale, v) -> scale to v.branch }.let { pillars ->
        Sets.combinations(pillars.toSet(), 2).asSequence().filter { twoPillars: Set<Pair<Scale, Branch>> ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          p1.second.trilogy() == p2.second.trilogy()
        }.map { twoPillars: Set<Pair<Scale, Branch>> ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          Triple(p1, p2, FlowScale.LARGE to flowLarge.branch)
        }.filter { (p1, p2, pFlow) -> trilogy(p1.second, p2.second, pFlow.second) != null }
          .map { (pair1: Pair<Scale, Branch>, pair2, pairFlow) -> TrilogyToFlow(setOf(pair1, pair2), pairFlow) }
          .toSet()
      }
    }
  }

  val branchOpposition = object : IFlowLargePatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch): Set<BranchOpposition> {
      return getScaleMap().entries.asSequence()
        .map { (scale, v) -> scale to v.branch }
        .filter { (_, branch) -> branch.opposite == flowLarge.branch }
        .map { (scale, branch) -> BranchOpposition(scale, branch, FlowScale.LARGE) }
        .toSet()
    }
  }
}

fun IEightWords.getFlowLargePatterns(flowLarge: IStemBranch): Set<FlowPattern> {
  return setOf(
    stemCombined, branchCombined, trilogyToFlow, branchOpposition
  ).flatMap { factory: IFlowLargePatternFactory ->
    with(factory) {
      this@getFlowLargePatterns.getPatterns(flowLarge)
    }
  }.toSet()
}
