/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui

import destiny.core.chinese.Branch
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement
import destiny.iching.Symbol
import java.io.Serializable

interface IMountainYinYang {
  fun getYinYang(m: Mountain): Boolean

  /** 陽山 */
  val yangMountains: Set<Mountain>
    get() {
      return Mountain.values().filter { m -> getYinYang(m) }.toSet()
    }

  /** 陰山 */
  val yinMountains: Set<Mountain>
    get() {
      return Mountain.values().subtract(yangMountains)
    }
}

/**
 * 玄空派三元派 ,
 * 三元盤的二十四山陰陽是依據天地人三元龍來確定的
 *
 * 甲庚丙壬、乾坤艮巽、寅申巳亥12山，均屬陽
 * 辰戌丑未、乙辛丁癸、子午卯酉12山，均屬陰
 *
 * 四正卦的地元龍甲庚丙壬皆屬陽；四隅卦的地元龍辰戍丑未皆屬陰。
 * 四正卦的天元龍子午卯酉皆屬陰；四隅卦的天元龍乾坤艮巽皆屬陽。
 * 四正卦的人元龍乙辛丁癸皆屬陰；四隅卦的人元龍寅申巳亥皆屬陽。
 *
 *
 * 寅－－內藏甲、丙、戊，已知甲丙屬陽，故寅為陽。
 * 申－－內藏庚、壬、戊，已知庚壬屬陽，故申屬陽。
 * 巳－－內藏庚、丙、戊，已知庚丙屬陽，故巳屬陽。
 * 亥－－內藏甲、壬、戊，已知甲壬屬陽，故亥屬陽。
 *
 * 辰－－內藏乙、癸、戊，已知乙癸屬陰，故辰屬陰。
 * 戌－－內藏辛、丁、戊，已知辛丁屬陰，故戌屬陰。
 * 丑－－內藏癸、辛、己，已知癸辛屬陰，故丑屬陰。
 * 未－－內藏丁、乙、己，己知丁乙屬陰，故未屬陰。
 *
 * 天元龍——陽：乾、艮、巽、坤，陰：子、午、卯、酉。
 * 地元龍——陽：甲、庚、壬、丙，陰：辰、戌、丑、未。
 * 人元龍——陽：寅、申、巳、亥，陰：乙、丁、辛、癸。
 *
 * 原文網址：https://kknews.cc/geomantic/jm5z686.html
 *
 * 參考圖檔 https://imgur.com/QiNaMbL
 *
 * */
class MountainYinYangEmptyImpl : IMountainYinYang, Serializable {
  override fun getYinYang(m: Mountain): Boolean {
    return when (m.mnt) {
      is SealedMnt.MntSymbol -> true
      is SealedMnt.MntStem -> // 陽干傳回陽 , 陰干傳回陰
        m.mnt.stem.booleanValue
      is SealedMnt.MntBranch -> {
        listOf(Branch.寅, Branch.巳, Branch.申, Branch.亥).contains(m.mnt.branch)
      }
    }
  }
}


/**
 * 三元盤
 * 與 玄空派 [MountainYinYangEmptyImpl] 的差別只有 [Symbol] 四卦
 * 玄空派 認為 乾、坤、艮、巽 ==> 陽
 * 三元派 認為 乾、坤、艮、巽 ==> 陰
 *
 * 參照： https://imgur.com/vQ6TDfh
 */
class MountainYinYangSanYuanImpl : IMountainYinYang, Serializable {
  override fun getYinYang(m: Mountain): Boolean {
    return when (m.mnt) {
      is SealedMnt.MntSymbol -> false
      is SealedMnt.MntStem -> // 陽干傳回陽 , 陰干傳回陰
        m.mnt.stem.booleanValue
      is SealedMnt.MntBranch -> {
        listOf(Branch.寅, Branch.巳, Branch.申, Branch.亥).contains(m.mnt.branch)
      }
    }
  }
}

/**
 * 三合派
 *
 * 三合盤的二十四山的陰陽則是依據先天八卦與洛書的關係，以及納甲之說和後天八卦四正卦的三合而制定的
 *
 * 壬、癸、寅、午、戌、申、子、辰此八山為陽
 * 甲、乙為陽
 * 巽、辛、丁、巳、酉、丑此六山為陰
 * 艮、丙、庚、亥、卯、未此六山為陰
 */
class MountainYinYangTrilogyImpl : IMountainYinYang, Serializable {
  override fun getYinYang(m: Mountain): Boolean {
    return when (m.mnt) {
      is SealedMnt.MntBranch -> {
        return when (BranchTools.trilogy(m.mnt.branch)) {
          FiveElement.水, FiveElement.火 -> true
          FiveElement.木, FiveElement.金 -> false
          FiveElement.土 -> throw RuntimeException("三合不可能為土 : ${m.mnt.branch}")
        }
      }
      is SealedMnt.MntStem -> {
        return when (m.mnt.stem.fiveElement) {
          FiveElement.水, FiveElement.木 -> true
          FiveElement.火, FiveElement.金 -> false
          FiveElement.土 -> throw RuntimeException("24山的天干不會有土 : ${m.mnt.stem}")
        }
      }
      is SealedMnt.MntSymbol -> {
        return when (m.mnt.symbol) {
          Symbol.乾, Symbol.坤 -> true
          Symbol.艮, Symbol.巽 -> false
          else -> throw RuntimeException("24山無此八卦 : ${m.mnt.symbol}")
        }
      }
    }
  }
}
