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

enum class Auspicious(val pillars : Set<Scale> = emptySet()) {
  天赦日(setOf(Scale.DAY)),
  玉堂日(setOf(Scale.DAY)),
  天德貴人(Scale.entries.toSet()),
  月德貴人(setOf(Scale.DAY , Scale.HOUR)),
  天德合(Scale.entries.toSet()),
  天乙貴人(Scale.entries.toSet()),
  天醫(setOf(Scale.YEAR, Scale.DAY, Scale.HOUR)),
}

enum class Inauspicious(val pillars: Set<Scale> = emptySet()) {
  受死日(setOf(Scale.DAY)),
  // 月 to 日為主 , 月、時次之 , 年再次之
  陰差陽錯(Scale.entries.toSet()),
  十惡大敗(Scale.entries.toSet()),
  四廢日(setOf(Scale.DAY)),
  羊刃(Scale.entries.toSet()),
}

sealed class IdentityPattern : IEightWordsPattern {
  data class StemCombined(val pillars: Set<Pair<Scale, Stem>>) : IdentityPattern()
  data class BranchCombined(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
  /** 地支三合 */
  data class Trilogy(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
  data class BranchOpposition(val pillars: Set<Pair<Scale, Branch>>) : IdentityPattern()
  /** 天干通根 */
  data class StemRooted(val pillar: Scale, val stem: Stem, val roots: Set<Pair<Scale, Branch>>) : IdentityPattern()

  /** 吉祥 [Auspicious] */
  data class AuspiciousPattern(val value : Auspicious, val pillars: Set<Scale> = emptySet()) : IdentityPattern() {
    init {
      require(value.pillars.containsAll(pillars)) {
        "Invalid pillars $pillars for ${value.name}, only allowed: ${value.pillars}"
      }
    }
  }

  /** 不祥 [Inauspicious] */
  data class InauspiciousPattern(val value : Inauspicious, val pillars: Set<Scale> = emptySet()) : IdentityPattern() {
    init {
      require(value.pillars.containsAll(pillars)) {
        "Invalid pillars $pillars for ${value.name}, only allowed: ${value.pillars}"
      }
    }
  }

}

sealed class FlowPattern : IEightWordsPattern {
  /** 五行生剋 */
  data class Affecting(val pillar: Scale, val stem: Stem, val reacting: Reacting, val flowScales: Set<FlowScale>) : FlowPattern()
  /** 天干相合 */
  data class StemCombined(val pillar: Scale, val stem: Stem, val flowScale: FlowScale) : FlowPattern()
  /** 地支相合 */
  data class BranchCombined(val pillar: Scale, val branch: Branch, val flowScale: FlowScale) : FlowPattern()
  /** 本命兩柱 與流運某干支 形成三合 */
  data class TrilogyToFlow(val pairs: Set<Pair<Scale, Branch>>, val flow: Pair<FlowScale, Branch>) : FlowPattern()
  /** 本命某柱 與流運兩柱 形成三合 */
  data class ToFlowTrilogy(val pillar: Scale, val branch: Branch, val flows: Set<Pair<FlowScale, Branch>>) : FlowPattern()
  /** 地支相沖 */
  data class BranchOpposition(val pillar: Scale, val branch: Branch, val flowScale: FlowScale) : FlowPattern()
}



