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

enum class Auspicious(val scales : Set<Scale> = emptySet()) {
  天赦日(setOf(Scale.DAY)),
  玉堂日(setOf(Scale.DAY)),
  天德貴人(Scale.entries.toSet()),
  月德貴人(setOf(Scale.DAY , Scale.HOUR)),
  天德合(Scale.entries.toSet()),
}

enum class Inauspicious {
  受死日,
  陰差陽錯,
}

sealed class IdentityPattern : IEightWordsPattern {
  data class StemCombined(val pillars: Set<Pair<Scale, Stem>>) : IdentityPattern()
  data class BranchCombined(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
  /** 地支三合 */
  data class Trilogy(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
  data class BranchOpposition(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
  /** 天干通根 */
  data class StemRooted(val scale: Scale, val stem: Stem, val roots: Set<Pair<Scale, Branch>>) : IdentityPattern()

  /** 吉祥日 [Auspicious] */
  data class AuspiciousPattern(val value : Auspicious, val scales: Set<Scale> = emptySet()) : IdentityPattern()

  /** 不祥日 [Inauspicious] */
  data class InauspiciousDay(val value : Inauspicious) : IdentityPattern()

}

sealed class FlowPattern : IEightWordsPattern {
  /** 五行生剋 */
  data class Affecting(val scale: Scale, val stem: Stem, val reacting: Reacting, val flowScales: Set<FlowScale>) : FlowPattern()
  /** 天干相合 */
  data class StemCombined(val scale: Scale, val stem: Stem, val flowScale: FlowScale) : FlowPattern()
  /** 地支相合 */
  data class BranchCombined(val scale: Scale, val branch: Branch, val flowScale: FlowScale) : FlowPattern()
  /** 本命兩柱 與流運某干支 形成三合 */
  data class TrilogyToFlow(val pairs: Set<Pair<Scale, Branch>>, val flow: Pair<FlowScale, Branch>) : FlowPattern()
  /** 本命某柱 與流運兩柱 形成三合 */
  data class ToFlowTrilogy(val scale: Scale, val branch: Branch, val flows: Set<Pair<FlowScale, Branch>>) : FlowPattern()
  /** 地支相沖 */
  data class BranchOpposition(val scale: Scale, val branch: Branch, val flowScale: FlowScale) : FlowPattern()
}



