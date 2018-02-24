/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:30:40
 */
package destiny.FengShui.SanYuan

import destiny.astrology.Utils
import destiny.iching.Symbol
import java.io.Serializable


/**
 * 後天八卦於羅盤上的位置
 */
class AcquiredSymbolCompass : AbstractSymbol<Symbol>(), Serializable {

  /**
   * 取得某個卦的起始度數
   */
  override fun getStartDegree(t: Symbol): Double {
    return Utils.getNormalizeDegree(symbolList.indexOf(t) * stepDegree + initDegree)
  }


  /**
   * 取得某個卦的結束度數
   */
  override fun getEndDegree(t: Symbol): Double {
    return Utils.getNormalizeDegree((symbolList.indexOf(t) + 1) * stepDegree + initDegree)
  }

  /**
   * 取得目前這個度數位於哪個卦當中
   */
  fun getSymbol(degree: Double): Any {
    var index = ((degree + 360 - initDegree) / stepDegree).toInt()
    if (index >= 8) index -= 8
    return symbolList[index]
  }

  companion object {
    private val symbolList = listOf(Symbol.坎, Symbol.艮, Symbol.震, Symbol.巽, Symbol.離, Symbol.坤, Symbol.兌, Symbol.乾)
  }
}
