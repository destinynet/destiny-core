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
import destiny.core.chinese.eightwords.FlowTranslator.translateAffecting
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.FlowTranslator.translateStemCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateToFlowTrilogy
import destiny.core.chinese.eightwords.FlowTranslator.translateTrilogyToFlow

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

fun IEightWords.yearKeyPoints(flowLarge: IStemBranch, flowYear: IStemBranch): List<String> {
  return getFlowLargeYearPatterns(flowLarge, flowYear).let {
    buildList {
      addAll(it.translateAffecting())
      addAll(it.translateStemCombined())
      addAll(it.translateBranchCombined())
      addAll(it.translateTrilogyToFlow())
      addAll(it.translateToFlowTrilogy())
      addAll(it.translateBranchOpposition())
    }
  }
}


/**
 * 本命四柱、大運 特徵
 */
fun IEightWords.fortuneLargeKeyPoints(flowLarge: IStemBranch): List<String> {
  return getFlowLargePatterns(flowLarge).let {
    buildList {
      addAll(it.translateAffecting())
      addAll(it.translateStemCombined())
      addAll(it.translateBranchCombined())
      addAll(it.translateTrilogyToFlow())
      addAll(it.translateToFlowTrilogy())
      addAll(it.translateBranchOpposition())
    }
  }
}

