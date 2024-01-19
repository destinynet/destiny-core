/**
 * Created by smallufo on 2024-01-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.IPattern
import destiny.core.Scale
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem


interface IEightWordsPattern : IPattern

enum class Reacting {
  SAME,
  PRODUCING,
  PRODUCED,
  DOMINATING,
  BEATEN
}

sealed class EightWordsFlowPattern : IEightWordsPattern {
  data class BothAffecting(val scale: Scale, val stem: Stem, val reacting: Reacting, val flowScales : Set<FlowScale>) : EightWordsFlowPattern()
  data class StemCombined(val scale: Scale, val stem: Stem, val flowScale: FlowScale) : EightWordsFlowPattern()
  data class BranchCombined(val scale: Scale, val branch: Branch, val flowScale: FlowScale): EightWordsFlowPattern()
  data class TrilogyToFlow(val pairs: Set<Pair<Scale, Branch>>, val flows: Pair<FlowScale, Branch>) : EightWordsFlowPattern()
  data class ToFlowTrilogy(val scale: Scale, val branch: Branch, val flows: Set<Pair<FlowScale, Branch>>) : EightWordsFlowPattern()
  data class BranchOpposition(val scale: Scale, val branch: Branch, val flowScale: FlowScale) : EightWordsFlowPattern()
}



