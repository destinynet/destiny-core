/**
 * Created by smallufo on 2015-05-13.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import destiny.iching.Symbol.*

enum class Yuan {
  UP,  // 上元
  MID, // 中元
  LOW  // 下元
}

/** 九宮飛星 , 卦序推演 */

object FlyingStar {
  val symbolPeriods: List<Symbol?> = listOf(null, 乾, 兌, 艮, 離, 坎, 坤, 震, 巽)


  /** 從中宮開始，飛佈 走幾步 , 順或逆 , 最後的數字是多少 */
  fun getValue(start: Int, steps: Int, reversed: Boolean): Int {
    return (start + steps * (if (reversed) (-1) else 1)).let {
      if (it > 9)
        return@let (it - 9)
      if (it < 1)
        return@let (it + 9)
      return@let it
    }
  }
}

