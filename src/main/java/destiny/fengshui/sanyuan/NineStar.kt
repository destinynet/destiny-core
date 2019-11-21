/**
 * Created by smallufo on 2018-03-01.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import destiny.iching.SymbolAcquired

enum class NineStar(val period: Int) {
  貪狼(1), // 坎
  巨門(2), // 坤
  祿存(3), // 震
  文曲(4), // 巽
  廉貞(5),
  武曲(6), // 乾
  破軍(7), // 兌
  左輔(8), // 艮
  右弼(9); // 離

  /** 取得對應的八卦 */
  val symbol: Symbol? = SymbolAcquired.getSymbolNullable(period)

  companion object {

    /** 透過數字，反查九星 */
    fun getStar(period: Int): NineStar {
      return values().first { it.period == period }
    }
  }
}
