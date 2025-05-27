/**
 * Created by smallufo on 2024-01-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.trilogyToFlow
import destiny.core.chinese.IStemBranch

/**
 * 流年、流月
 */
interface IFlowYearMonthPatternFactory {
  fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<FlowPattern>
}

object FlowYearMonthPatterns {

  val bothAffecting = object : IFlowYearMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BothAffecting> {
      return detectBothAffectingGeneric(flowYear, FlowScale.YEAR, flowMonth, FlowScale.MONTH)
    }
  }

  val stemCombined = object : IFlowYearMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<StemCombined> {
      return detectStemCombinedGeneric(flowYear, FlowScale.YEAR, flowMonth, FlowScale.MONTH)
    }
  }

  val branchCombined = object : IFlowYearMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BranchCombined> {
      return detectBranchCombinedGeneric(flowYear, FlowScale.YEAR, flowMonth, FlowScale.MONTH)
    }
  }

  val trilogyToFlow = object : IFlowYearMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<TrilogyToFlow> {
      return detectTrilogyToFlowGeneric(flowYear, FlowScale.YEAR, flowMonth, FlowScale.MONTH)
    }
  }

  val toFlowTrilogy = object : IFlowYearMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<ToFlowTrilogy> {
      return detectToFlowTrilogyGeneric(flowYear, FlowScale.YEAR, flowMonth, FlowScale.MONTH)
    }
  }

  val branchOpposition = object : IFlowYearMonthPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BranchOpposition> {
      return detectBranchOppositionGeneric(flowYear, FlowScale.YEAR, flowMonth, FlowScale.MONTH)
    }
  }
}


fun IEightWords.getFlowYearMonthPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<FlowPattern> {
  return setOf(
    bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition
  ).flatMap { factory: IFlowYearMonthPatternFactory ->
    with(factory) {
      this@getFlowYearMonthPatterns.getPatterns(flowYear, flowMonth)
    }
  }.toSet()
}

