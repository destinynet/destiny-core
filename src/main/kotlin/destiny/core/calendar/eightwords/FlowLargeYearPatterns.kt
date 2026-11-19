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
import destiny.core.chinese.eightwords.EwEvent
import destiny.core.chinese.eightwords.FlowDtoTransformer.toAffectingDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toToFlowTrilogyDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toTrilogyToFlowDtos

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

/** 偵測出來的流運格局 → 帶描述的 DTO（大運、歲運、流年流月共用同一套轉換） */
private fun Set<FlowPattern>.toFlowDtos(): Set<EwEvent.EwFlow> = buildSet {
  addAll(this@toFlowDtos.filterIsInstance<Affecting>().toAffectingDtos())
  addAll(this@toFlowDtos.filterIsInstance<StemCombined>().toStemCombinedDtos())
  addAll(this@toFlowDtos.filterIsInstance<BranchCombined>().toBranchCombinedDtos())
  addAll(this@toFlowDtos.filterIsInstance<TrilogyToFlow>().toTrilogyToFlowDtos())
  addAll(this@toFlowDtos.filterIsInstance<ToFlowTrilogy>().toToFlowTrilogyDtos())
  addAll(this@toFlowDtos.filterIsInstance<BranchOpposition>().toBranchOppositionDtos())
}

/**
 * 本命四柱、大運 特徵
 */
fun IEightWords.fortuneLargeDtos(flowLarge: IStemBranch) : Set<EwEvent.EwFlow> =
  getFlowLargePatterns(flowLarge).toFlowDtos()

/** 大運、流年 特徵 */
fun IEightWords.yearDtos(flowLarge: IStemBranch, flowYear: IStemBranch): Set<EwEvent.EwFlow> =
  getFlowLargeYearPatterns(flowLarge, flowYear).toFlowDtos()

/** 流年、流月 特徵 */
fun IEightWords.monthDtos(flowYear: IStemBranch, flowMonth: IStemBranch): Set<EwEvent.EwFlow> =
  getFlowYearMonthPatterns(flowYear, flowMonth).toFlowDtos()
