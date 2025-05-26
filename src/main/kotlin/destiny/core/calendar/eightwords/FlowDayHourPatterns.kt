/**
 * Created by smallufo on 2025-05-26.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.calendar.eightwords.FlowDayHourPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowDayHourPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowDayHourPatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.chinese.IStemBranch

/**
 * 流日、流時
 */
interface IFlowDayHourPatternFactory {
  fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<FlowPattern>
}

object FlowDayHourPatterns {

  val bothAffecting = object : IFlowDayHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<BothAffecting> {
      return detectBothAffectingGeneric(flowDay, FlowScale.DAY, flowHour, FlowScale.HOUR)
    }
  }

  val stemCombined = object : IFlowDayHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<StemCombined> {
      return detectStemCombinedGeneric(flowDay, FlowScale.DAY, flowHour, FlowScale.HOUR)
    }
  }

  val branchCombined = object : IFlowDayHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<BranchCombined> {
      return detectBranchCombinedGeneric(flowDay, FlowScale.DAY, flowHour, FlowScale.HOUR)
    }
  }

  val trilogyToFlow = object : IFlowDayHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<TrilogyToFlow> {
      return detectTrilogyToFlowGeneric(flowDay, FlowScale.DAY, flowHour, FlowScale.HOUR)
    }
  }

  val toFlowTrilogy = object : IFlowDayHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<ToFlowTrilogy> {
      return detectToFlowTrilogyGeneric(flowDay, FlowScale.DAY, flowHour, FlowScale.HOUR)
    }
  }

  val branchOpposition = object : IFlowDayHourPatternFactory {
    override fun IEightWords.getPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<BranchOpposition> {
      return detectBranchOppositionGeneric(flowDay, FlowScale.DAY, flowHour, FlowScale.HOUR)
    }
  }
}


fun IEightWords.getFlowHourPatterns(flowDay: IStemBranch, flowHour: IStemBranch): Set<FlowPattern> {
  return setOf(bothAffecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition).flatMap { factory ->
    with(factory) { this@getFlowHourPatterns.getPatterns(flowDay, flowHour) }
  }.toSet()
}
