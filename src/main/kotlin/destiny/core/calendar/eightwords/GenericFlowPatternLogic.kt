/**
 * Created by smallufo on 2025-05-27.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement.Companion.beatenCount
import destiny.core.chinese.FiveElement.Companion.dominatorCount
import destiny.core.chinese.FiveElement.Companion.producedCount
import destiny.core.chinese.FiveElement.Companion.producingCount
import destiny.core.chinese.FiveElement.Companion.sameCount
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.trilogy

/** 五行生剋 */
internal fun IEightWords.detectAffectingGeneric(flow1: IStemBranch, flow1Scale: FlowScale, flow2: IStemBranch, flow2Scale: FlowScale): Set<Affecting> {
  return getScaleMap().entries.asSequence()
    .map { (scale, v) -> scale to v.stem }
    .mapNotNull { (scale, stem) ->
      val actualFlowScales = setOf(flow1Scale, flow2Scale)
      when {
        stem.fiveElement.sameCount(flow1.stem, flow2.stem) == 2      -> Affecting(scale, stem, Reacting.SAME, actualFlowScales)
        stem.fiveElement.producingCount(flow1.stem, flow2.stem) == 2 -> Affecting(scale, stem, Reacting.PRODUCING, actualFlowScales)
        stem.fiveElement.producedCount(flow1.stem, flow2.stem) == 2  -> Affecting(scale, stem, Reacting.PRODUCED, actualFlowScales)
        stem.fiveElement.dominatorCount(flow1.stem, flow2.stem) == 2 -> Affecting(scale, stem, Reacting.DOMINATING, actualFlowScales)
        stem.fiveElement.beatenCount(flow1.stem, flow2.stem) == 2    -> Affecting(scale, stem, Reacting.BEATEN, actualFlowScales)
        else                                                         -> null
      }
    }.toSet()
}


internal fun IEightWords.detectStemCombinedGeneric(flow1: IStemBranch, flow1Scale: FlowScale, flow2: IStemBranch, flow2Scale: FlowScale): Set<StemCombined> {
  return getScaleMap().entries.asSequence()
    .map { (scale, v) -> scale to v.stem }
    .flatMap { (scale, stem) ->
      buildSet {
        if (stem.combined.first == flow1.stem)
          add(StemCombined(scale, stem, flow1Scale))
        if (stem.combined.first == flow2.stem)
          add(StemCombined(scale, stem, flow2Scale))
      }
    }.toSet()
}

internal fun IEightWords.detectBranchCombinedGeneric(flow1: IStemBranch, flow1Scale: FlowScale, flow2: IStemBranch, flow2Scale: FlowScale): Set<BranchCombined> {
  return getScaleMap().entries.asSequence()
    .map { (scale, v) -> scale to v.branch }
    .flatMap { (scale, branch) ->
      buildSet {
        if (branch.combined == flow1.branch)
          add(BranchCombined(scale, branch, flow1Scale))
        if (branch.combined == flow2.branch)
          add(BranchCombined(scale, branch, flow2Scale))
      }
    }.toSet()
}

internal fun IEightWords.detectTrilogyToFlowGeneric(
  flow1: IStemBranch,
  flow1Scale: FlowScale,
  flow2: IStemBranch,
  flow2Scale: FlowScale
): Set<TrilogyToFlow> {
  return getScaleMap().entries.map { (scale, v) -> scale to v.branch }
    .let { pillars: List<Pair<Scale, Branch>> ->
      Sets.combinations(pillars.toSet(), 2).asSequence()
        .filter { twoPillars: Set<Pair<Scale, Branch>> ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          p1.second.trilogy() == p2.second.trilogy()
        }
        .flatMap { twoPillars: Set<Pair<Scale, Branch>> ->
          val (p1, p2) = twoPillars.toList().let { it[0] to it[1] }
          // 創建與 flow1 和 flow2 的組合
          setOf(
            Triple(p1, p2, flow1Scale to flow1.branch),
            Triple(p1, p2, flow2Scale to flow2.branch)
          )
        }
        .filter { (p1, p2, pFlow) ->
          // 檢查 p1, p2 和 pFlow.second (flow 的地支) 是否能構成三合
          trilogy(p1.second, p2.second, pFlow.second) != null
        }
        .map { (pair1, pair2, pairFlow) ->
          TrilogyToFlow(setOf(pair1, pair2), pairFlow)
        }
    }.toSet()
}

internal fun IEightWords.detectToFlowTrilogyGeneric(
  flow1: IStemBranch,
  flow1Scale: FlowScale,
  flow2: IStemBranch,
  flow2Scale: FlowScale
): Set<ToFlowTrilogy> {
  return getScaleMap().entries.asSequence()
    .map { (scale, v) -> scale to v.branch }
    .mapNotNull { (scale, branch) ->
      // 檢查本命的一個地支與兩個 flow 的地支是否能構成三合
      if (trilogy(branch, flow1.branch, flow2.branch) != null)
        ToFlowTrilogy(scale, branch, setOf(flow1Scale to flow1.branch, flow2Scale to flow2.branch))
      else
        null
    }.toSet()
}

internal fun IEightWords.detectBranchOppositionGeneric(
  flow1: IStemBranch,
  flow1Scale: FlowScale,
  flow2: IStemBranch,
  flow2Scale: FlowScale
): Set<BranchOpposition> {
  return getScaleMap().entries.asSequence()
    .map { (scale, v) -> scale to v.branch }
    .flatMap { (scale, branch) ->
      buildSet {
        if (branch.opposite == flow1.branch)
          add(BranchOpposition(scale, branch, flow1Scale))
        if (branch.opposite == flow2.branch)
          add(BranchOpposition(scale, branch, flow2Scale))
      }
    }.toSet()
}

