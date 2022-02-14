package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*

/** 九宮飛星 , 卦序推演 */
object FlyingStar {

  val symbolPeriods: List<Symbol?> = listOf(null, 乾, 兌, 艮, 離, 坎, 坤, 震, 巽)

  /** 從中宮 (symbol=null) 開始飛佈 , 走幾步 , 順或逆 , 最後的數字是多少 */
  fun getValue(start: Period, steps: Int, reversed: Boolean = false): Period {

    return (start.value + steps * (if (reversed) (-1) else 1)).toPeriod()
  }

}
