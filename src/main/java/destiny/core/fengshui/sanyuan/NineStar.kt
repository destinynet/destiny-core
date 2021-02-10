/**
 * Created by smallufo on 2018-03-01.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.ILoop
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired

enum class NineStar(val period: Int , val color : Char , val fiveElement: FiveElement) : ILoop<NineStar> {
  貪狼(1 , '白' , 水), // 一白水星 , 坎
  巨門(2 , '黑' , 土), // 二黑土星 , 坤
  祿存(3 , '碧' , 木), // 三碧木星 , 震
  文曲(4 , '綠' , 木), // 四綠木星 , 巽
  廉貞(5 , '黃' , 土), // 五黃土星
  武曲(6 , '白' , 金), // 六白金星 , 乾
  破軍(7 , '赤' , 金), // 七赤金星 , 兌
  左輔(8 , '白' , 土), // 八白土星 , 艮
  右弼(9 , '紫' , 火); // 九紫火星 , 離

  /** 取得對應的八卦 */
  val symbol: Symbol? = SymbolAcquired.getSymbolNullable(period)

  override fun next(n: Int): NineStar {
    return (this.period + n).toStar()
  }

  companion object {

    /** 透過數字，反查九星 */
    fun Int.toStar(): NineStar {
      return of(this)
    }

    fun of(period: Int): NineStar {
      return if (period in 1..9) {
        values().first { it.period == period }
      } else {
        val newPeriod = (period % 9).let { r -> if (r == 0) 9 else r }.let { v -> if (v < 1) v + 9 else v }
        of(newPeriod)
      }
    }

  }
}
