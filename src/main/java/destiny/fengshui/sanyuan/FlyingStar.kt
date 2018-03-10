package destiny.fengshui.sanyuan

import destiny.iching.Symbol

/** 九宮飛星 , 卦序推演 */

object FlyingStar {
  val symbolPeriods: List<Symbol?> = listOf(null, Symbol.乾, Symbol.兌,
                                            Symbol.艮, Symbol.離,
                                            Symbol.坎, Symbol.坤,
                                            Symbol.震, Symbol.巽)



  /** 從中宮 (symbol=null) 開始飛佈 , 走幾步 , 順或逆 , 最後的數字是多少 */
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