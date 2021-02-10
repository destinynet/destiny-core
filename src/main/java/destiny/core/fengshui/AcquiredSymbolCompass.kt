/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:30:40
 */
package destiny.core.fengshui

import destiny.iching.Symbol
import destiny.iching.Symbol.*
import destiny.tools.circleUtils
import java.io.Serializable

/**
 * 後天八卦於羅盤上的位置
 */
class AcquiredSymbolCompass : AbstractSymbolCompass(), Serializable {

  /**
   * 取得某個卦的起始度數
   */
  override fun getStartDegree(t: Symbol): Double {
    return circleUtils.getNormalizeDegree(
      symbolList.indexOf(t) * stepDegree + initDegree)
  }


  /**
   * 取得某個卦的結束度數
   */
  override fun getEndDegree(t: Symbol): Double {
    return circleUtils.getNormalizeDegree((symbolList.indexOf(t) + 1) * stepDegree + initDegree)
  }


  /**
   * 取得目前這個度數位於哪個卦當中
   */
  override fun get(degree: Double): Symbol {
    val index = with(circleUtils) {
      (degree.aheadOf(initDegree) / stepDegree).toInt()
    }
    return symbolList[index]
  }

  companion object {
    private val symbolList = listOf(坎, 艮, 震, 巽, 離, 坤, 兌, 乾)
  }
}
