/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.affecting
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.eightwords.FlowDtoTransformer.toAffectingDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toToFlowTrilogyDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toTrilogyToFlowDtos
import destiny.core.electional.Dtos

/**
 * 大運、流年
 */
interface IFlowLargeYearPatternFactory {
  fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern>
}

object FlowLargeYearPatterns {

  val affecting = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<Affecting> {
      return detectAffectingGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
    }
  }

  val stemCombined = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<StemCombined> {
      return detectStemCombinedGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
    }
  }

  val branchCombined = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BranchCombined> {
      return detectBranchCombinedGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
    }
  }

  val trilogyToFlow = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<TrilogyToFlow> {
      return detectTrilogyToFlowGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
    }
  }

  val toFlowTrilogy = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<ToFlowTrilogy> {
      return detectToFlowTrilogyGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
    }
  }

  val branchOpposition = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BranchOpposition> {
      return detectBranchOppositionGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
    }
  }
}

fun IEightWords.getFlowLargeYearPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern> {
  return setOf(
    affecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition
  ).flatMap { factory: IFlowLargeYearPatternFactory ->
    with(factory) {
      this@getFlowLargeYearPatterns.getPatterns(flowLarge, flowYear)
    }
  }.toSet()
}

/**
 * 本命四柱、大運 特徵
 */
fun IEightWords.fortuneLargeDtos(flowLarge: IStemBranch) : Set<Dtos.EwEvent.EwFlow> {
  return getFlowLargePatterns(flowLarge).let { patterns: Set<FlowPattern> ->
    buildSet {
      addAll(patterns.filterIsInstance<Affecting>().toAffectingDtos())
      addAll(patterns.filterIsInstance<StemCombined>().toStemCombinedDtos())
      addAll(patterns.filterIsInstance<BranchCombined>().toBranchCombinedDtos())
      addAll(patterns.filterIsInstance<TrilogyToFlow>().toTrilogyToFlowDtos())
      addAll(patterns.filterIsInstance<ToFlowTrilogy>().toToFlowTrilogyDtos())
      addAll(patterns.filterIsInstance<BranchOpposition>().toBranchOppositionDtos())
    }
  }
}


/** 大運、流年 特徵 */
fun IEightWords.yearDtos(flowLarge: IStemBranch, flowYear: IStemBranch): Set<Dtos.EwEvent.EwFlow> {
  return getFlowLargeYearPatterns(flowLarge, flowYear).let { patterns: Set<FlowPattern> ->
    buildSet {
      addAll(patterns.filterIsInstance<Affecting>().toAffectingDtos())
      addAll(patterns.filterIsInstance<StemCombined>().toStemCombinedDtos())
      addAll(patterns.filterIsInstance<BranchCombined>().toBranchCombinedDtos())
      addAll(patterns.filterIsInstance<TrilogyToFlow>().toTrilogyToFlowDtos())
      addAll(patterns.filterIsInstance<ToFlowTrilogy>().toToFlowTrilogyDtos())
      addAll(patterns.filterIsInstance<BranchOpposition>().toBranchOppositionDtos())
    }
  }
}


