/**
 * Created by smallufo on 2019-02-28.
 */
package destiny.core.chinese

import destiny.core.Gender
import destiny.core.Gender.*
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.Symbol
import java.io.Serializable

interface IYuanGander {
  /**
   * @param yinYang 陽男、陽女、陰男、 or 陰女
   */
  fun getSymbol(gender: Gender, yuan: Yuan, yinYang: IYinYang) : Symbol
}

class YuanGenderImpl : IYuanGander, Serializable {
  override fun getSymbol(gender: Gender, yuan: Yuan, yinYang: IYinYang) : Symbol {
    return when (yuan) {
      Yuan.UP -> when (gender) {
        男 -> Symbol.艮
        女 -> Symbol.坤
      }
      Yuan.MID -> if (gender === 女 && yinYang.booleanValue || gender === 男 && !yinYang.booleanValue)
        Symbol.坤
      else
        Symbol.艮
      Yuan.LOW -> when (gender) {
        男 -> Symbol.離
        女 -> Symbol.兌
      }
    }
  }
}