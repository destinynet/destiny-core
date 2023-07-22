/**
 * Created by smallufo on 2018-03-01.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.ILoop
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*
import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import destiny.core.iching.Symbol
import destiny.core.iching.SymbolAcquired
import destiny.tools.ArrayTools

enum class NineStar(val period: Period, val color: Char, val fiveElement: FiveElement) : ILoop<NineStar> {
  貪狼(1.toPeriod(), '白', 水), // 一白水星 , 坎
  巨門(2.toPeriod(), '黑', 土), // 二黑土星 , 坤
  祿存(3.toPeriod(), '碧', 木), // 三碧木星 , 震
  文曲(4.toPeriod(), '綠', 木), // 四綠木星 , 巽
  廉貞(5.toPeriod(), '黃', 土), // 五黃土星
  武曲(6.toPeriod(), '白', 金), // 六白金星 , 乾
  破軍(7.toPeriod(), '赤', 金), // 七赤金星 , 兌
  左輔(8.toPeriod(), '白', 土), // 八白土星 , 艮
  右弼(9.toPeriod(), '紫', 火); // 九紫火星 , 離

  /** 取得對應的八卦 */
  val symbol: Symbol? = SymbolAcquired.getSymbolNullable(period.value)

  override fun next(n: Int): NineStar {
    return (this.period + n).value.toStar()
  }

  companion object {

    /** 透過數字，反查九星 */
    fun Int.toStar(): NineStar {
      return ArrayTools[entries.toTypedArray(), this - 1]
    }

    fun of(period: Period): NineStar {
      return period.value.toStar()
    }

  }
}
