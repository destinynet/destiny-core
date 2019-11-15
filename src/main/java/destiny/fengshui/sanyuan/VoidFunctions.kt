/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui.sanyuan

import destiny.core.chinese.Branch
import destiny.fengshui.EarthlyCompass
import destiny.fengshui.Mountain
import destiny.fengshui.SealedMnt
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import kotlin.math.abs

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
      it.symbol === symbol && tri === getTri(it)
    }
  }

  /** 城門訣 , 傳回 正城門、副城門 */
  fun getGates(m: Mountain) : Map<Gate , Mountain> {
    // 先取出「向」兩旁的卦
    val 地盤 = EarthlyCompass()
    val dirSymbol: Symbol = 地盤.getSymbol(m.opposite)

    /*
    取出 「向」卦，順逆兩卦，相對應（天元、人元、地元龍）的兩個山
    例如：子山午向，「向」為離卦。 離卦 兩旁分別是 巽、坤兩卦
    而巽、坤兩卦內，的天元龍就是「巽」「坤」兩山
     */
    val mountains: Set<Mountain> = setOf(
      getMappingMountain(m, SymbolAcquired.getClockwiseSymbol(dirSymbol)),
      getMappingMountain(m, SymbolAcquired.getCounterClockwiseSymbol(dirSymbol)))

    // 目前有兩個山，其中一個為「正城門」、另一個為「副城門」.
    // 正者：其卦 與「向」卦 的落書數，相差5 , 另一個為負
    // 例如：子山午向，「向卦」為離，洛書數為9
    // 旁邊兩山，「巽=4」「坤=2」 , 「巽」的4 與 「離」的9 相差5 , 故，「巽」為正城門、「坤」為副城門

    // 正城門
    val mntPrimary = mountains.first {
      val mntSymbol: Symbol = 地盤.getSymbol(it)
      abs(SymbolAcquired.getIndex(mntSymbol) - SymbolAcquired.getIndex(dirSymbol)) == 5
    }
    // 副城門
    val mntSecondary = mountains.minus(mntPrimary).first()
    return mapOf(
      Gate.正城門 to mntPrimary ,
      Gate.副城門 to mntSecondary)
  }
}