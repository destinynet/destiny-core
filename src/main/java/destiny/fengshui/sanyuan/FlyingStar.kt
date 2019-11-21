package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import destiny.iching.Symbol.*

/** 九宮飛星 , 卦序推演 */

object FlyingStar {

  val symbolPeriods: List<Symbol?> = listOf(null, 乾, 兌, 艮, 離, 坎, 坤, 震, 巽)

  /** 從中宮 (symbol=null) 開始飛佈 , 走幾步 , 順或逆 , 最後的數字是多少 */
  fun getValue(start: Int, steps: Int, reversed: Boolean = false): Int {
    return (start + steps * (if (reversed) (-1) else 1)).let {
      when {
        it > 9 -> (it - 9)
        it < 1 -> (it + 9)
        else -> it
      }
    }
  }

}
