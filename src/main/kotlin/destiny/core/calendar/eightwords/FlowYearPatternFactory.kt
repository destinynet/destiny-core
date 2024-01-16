/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale
import destiny.core.calendar.eightwords.IEightWordsFlowYearPattern.StemCombined
import destiny.core.chinese.IStemBranch


interface IFlowYearPatternFactory {
  fun IEightWords.getPatterns(flowYear: IStemBranch): Set<IEightWordsFlowYearPattern>
}

object FlowYearPatterns {
  val stemCombined = object : IFlowYearPatternFactory {
    override fun IEightWords.getPatterns(flowYear: IStemBranch): Set<StemCombined> {
      val scales = getScaleMap().entries.asSequence().map { (scale: Scale, v) -> scale to v.stem }.filter { (_ , stem) ->
        stem.combined.first == flowYear.stem
      }.map { (scale , _) -> scale }
        .toSet()
      return setOf(StemCombined(scales , flowYear.stem.combined.first))
    }
  }
}
