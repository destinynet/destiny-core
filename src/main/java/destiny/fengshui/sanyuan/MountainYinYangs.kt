/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

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
      return Mountain.values().filter { m -> !getYinYang(m) }.toSet()
    }
}

/**
 * 玄空派
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
