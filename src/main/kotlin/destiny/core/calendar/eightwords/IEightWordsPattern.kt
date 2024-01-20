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

sealed class IdentityPattern : IEightWordsPattern {
  data class Trilogy(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
}

sealed class FlowPattern : IEightWordsPattern {
  data class BothAffecting(val scale: Scale, val stem: Stem, val reacting: Reacting, val flowScales : Set<FlowScale>) : FlowPattern()
  data class StemCombined(val scale: Scale, val stem: Stem, val flowScale: FlowScale) : FlowPattern()
  data class BranchCombined(val scale: Scale, val branch: Branch, val flowScale: FlowScale): FlowPattern()
  data class TrilogyToFlow(val pairs: Set<Pair<Scale, Branch>>, val flows: Pair<FlowScale, Branch>) : FlowPattern()
  data class ToFlowTrilogy(val scale: Scale, val branch: Branch, val flows: Set<Pair<FlowScale, Branch>>) : FlowPattern()
  data class BranchOpposition(val scale: Scale, val branch: Branch, val flowScale: FlowScale) : FlowPattern()
}



