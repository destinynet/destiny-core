/**
 * Created by smallufo on 2018-03-05.
 */
package destiny.fengshui

import destiny.core.chinese.Branch
import destiny.core.chinese.SimpleBranch
import destiny.core.chinese.StemBranch
import destiny.iching.Hexagram
import destiny.iching.Symbol
import destiny.iching.SymbolPositions
import destiny.iching.divine.Divines
import destiny.iching.divine.ISettingsOfStemBranch
import destiny.iching.divine.Relative
import destiny.iching.divine.SettingsGingFang

enum class GhostType {
  正,
  地, // 坐山的後天八曜煞. 後天的乾位，為先天的艮卦 . 艮的曜煞在寅. ==> 寅為乾卦的地曜煞。
  天  // 坐山先天的八曜煞. 先天的乾位，為後天的離卦 . 離的曜煞在亥. ==> 亥為乾卦的天曜煞。
}

/** 八曜煞 , 或稱「三曜煞」 */
object EightGhost {

  /** 正 曜煞 */
  private fun getStandardGhosts(symbol: Symbol, settings: ISettingsOfStemBranch): List<Branch> {
    val hexagram = Hexagram.Companion.getHexagram(symbol, symbol)
    return (1..6).filter {
      val sb: StemBranch = settings.getStemBranch(hexagram, it)
      val 外在五行 = SimpleBranch.getFiveElement(sb.branch)
      Divines.getRelative(外在五行, symbol.fiveElement) === Relative.官鬼
    }.map { settings.getStemBranch(hexagram, it).branch }
  }

  fun getGhosts(symbol: Symbol, type: GhostType, settings: ISettingsOfStemBranch): List<Branch> {
    return when (type) {
      GhostType.正 -> getStandardGhosts(symbol, settings)
      GhostType.地 -> getStandardGhosts(SymbolPositions.acquiredToCongenital(symbol), settings)
      GhostType.天 -> getStandardGhosts(SymbolPositions.congenitalToAcquired(symbol), settings)
    }
  }

  fun getGhost(symbol: Symbol,
               type: GhostType? = GhostType.正,
               settings: ISettingsOfStemBranch? = SettingsGingFang()): Branch {
    return getGhosts(symbol, type?:GhostType.正, settings ?: SettingsGingFang()).first()
  }

}