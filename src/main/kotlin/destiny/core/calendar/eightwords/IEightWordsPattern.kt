/**
 * Created by smallufo on 2024-01-15.
 */
package destiny.core.calendar.eightwords

import destiny.core.IPattern
import destiny.core.Scale
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem


interface IEightWordsPattern : IPattern

interface IEightWordsFlowYearPattern : IEightWordsPattern

interface IEightWordsFlowMonthPattern : IEightWordsFlowYearPattern

enum class Reacting {
  SAME , PRODUCING , PRODUCED , DOMINATING , BEATEN
}

sealed class BothAffecting(open val scale: Scale, open val stem: Stem, val reacting: Reacting) : IEightWordsFlowMonthPattern {
  data class Same(override val scale: Scale, override val stem: Stem) : BothAffecting(scale, stem, Reacting.SAME)
  data class Producing(override val scale: Scale, override val stem: Stem) : BothAffecting(scale, stem, Reacting.PRODUCING)
  data class Produced(override val scale: Scale, override val stem: Stem) : BothAffecting(scale, stem, Reacting.PRODUCED)
  data class Dominating(override val scale: Scale, override val stem: Stem) : BothAffecting(scale, stem, Reacting.DOMINATING)
  data class Beaten(override val scale: Scale, override val stem: Stem) : BothAffecting(scale, stem, Reacting.BEATEN)
}

data class StemCombined(val scale: Scale, val stem: Stem, val flowScale: Scale) : IEightWordsFlowMonthPattern

data class BranchCombined(val scale: Scale, val branch: Branch, val flowScale: Scale) : IEightWordsFlowMonthPattern

data class TrilogyToFlow(val pairs: Set<Pair<Scale, Branch>>, val pairFlow: Pair<Scale, Branch>) : IEightWordsFlowMonthPattern

data class ToFlowTrilogy(val scale: Scale, val branch: Branch, val flowYear: Pair<Scale, Branch>, val flowMonth: Pair<Scale, Branch>) : IEightWordsFlowMonthPattern