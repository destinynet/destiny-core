/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

import destiny.core.chinese.Branch
import destiny.iching.Symbol

/** 「玄空」所使用的 functions */
object VoidFunctions {

  /** 天元龍、人元龍、地元龍 */
  enum class Tri {
    天 ,   // 天元龍，中間
    人 ,   // 人元龍，順時針
    地     // 地元龍，逆時針
  }

  /** 查詢此山，為「三元龍」的哪一個 */
  private fun getTri(m: Mountain) : Tri {
    return when(m.mnt) {
      is SealedMnt.MntSymbol -> Tri.天 // 乾坤艮巽 , 都是天元龍 (中間)
      is SealedMnt.MntStem -> {
        if (m.mnt.stem.booleanValue) Tri.地 else Tri.人
      }
      is SealedMnt.MntBranch -> {
        return when(m.mnt.branch) {
          Branch.子 , Branch.卯 , Branch.午 , Branch.酉 -> Tri.天
          Branch.丑 , Branch.辰 , Branch.未 , Branch.戌 -> Tri.地
          Branch.寅 , Branch.巳 , Branch.申 , Branch.亥 -> Tri.人
        }
      }
    }
  }

  /**
   * 此山 , 在此 卦 中，對應的 山，是哪個山
   * 要找出 對應的 天元龍、人元龍、或 地元龍 的山
   *
   * 例如：子山，若對應到震卦
   * 先找出「子」，位於「坎卦」的「天元龍」
   * 再找出「震」卦的天元龍 ==> 卯 (最終傳回值)
   */
  fun getMappingMountain(m: Mountain, symbol: Symbol): Mountain {

    val tri = getTri(m)
    return Mountain.values().first {
      it.symbol === symbol && tri === VoidFunctions.getTri(it)
    }
  }
}