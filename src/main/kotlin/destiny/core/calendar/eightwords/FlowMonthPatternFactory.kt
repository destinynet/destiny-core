/**
 * Created by smallufo on 2024-01-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale
import destiny.core.chinese.FiveElement.Companion.beatenCount
import destiny.core.chinese.FiveElement.Companion.dominatorCount
import destiny.core.chinese.FiveElement.Companion.producedCount
import destiny.core.chinese.FiveElement.Companion.producingCount
import destiny.core.chinese.FiveElement.Companion.sameCount
import destiny.core.chinese.IStemBranch


interface IFlowMonthPatternFactory {
  fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<IEightWordsFlowMonthPattern>
}

val bothAffecting = object : IFlowMonthPatternFactory {
  override fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<BothAffecting> {
    return getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.map { (scale, stem) ->
      if (stem.fiveElement.sameCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Same(scale, stem)
      } else if (stem.fiveElement.producingCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Producing(scale, stem)
      } else if (stem.fiveElement.producedCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Produced(scale, stem)
      } else if (stem.fiveElement.dominatorCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Dominating(scale, stem)
      } else if (stem.fiveElement.beatenCount(flowYear.stem, flowMonth.stem) == 2) {
        BothAffecting.Beaten(scale, stem)
      } else {
        null
      }
    }.filterNotNull().toSet()
  }
}

fun IEightWords.getPatterns(flowYear: IStemBranch, flowMonth: IStemBranch): Set<IEightWordsFlowMonthPattern> {
  return setOf(
    bothAffecting
  ).flatMap { factory: IFlowMonthPatternFactory ->
    with(factory) {
      this@getPatterns.getPatterns(flowYear, flowMonth)
    }
  }.toSet()
}

