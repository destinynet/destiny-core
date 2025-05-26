/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.chinese.IStemBranch

/**
 * 大運、流年
 */
interface IFlowLargeYearPatternFactory {
  fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern>
}

object FlowLargeYearPatterns {

  val bothAffecting = object : IFlowLargeYearPatternFactory {
    override fun IEightWords.getPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<BothAffecting> {
      return detectBothAffectingGeneric(flowLarge, FlowScale.LARGE, flowYear, FlowScale.YEAR)
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

fun IEightWords.getFlowYearPatterns(flowLarge: IStemBranch, flowYear: IStemBranch): Set<FlowPattern> {
  return setOf(
    bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition
  ).flatMap { factory: IFlowLargeYearPatternFactory ->
    with(factory) {
      this@getFlowYearPatterns.getPatterns(flowLarge, flowYear)
    }
  }.toSet()
}
