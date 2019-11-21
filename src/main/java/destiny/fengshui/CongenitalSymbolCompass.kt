/**
 * Created by smallufo on 2019-11-21.
 */
package destiny.fengshui

import destiny.iching.Symbol
import destiny.iching.Symbol.*
import destiny.tools.circleUtils
import java.io.Serializable

/**
 * 先天八卦於羅盤上的位置
 */
class CongenitalSymbolCompass : AbstractSymbolCompass(), Serializable {

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
    private val symbolList = listOf(坤, 震, 離, 兌, 乾 , 巽, 坎, 艮)
  }

}
