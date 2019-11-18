/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.fengshui

import destiny.iching.Symbol
import destiny.tools.circleUtils

abstract class AbstractMountainCompass : ICompass<Mountain> {

  /**
   * 取得 "子" 山的起始度數 (地盤正針得傳回 352.5 度)
   * 由繼承此 Class 的子物件去實作
   */
  abstract override val initDegree: Double

  override val stepDegree: Double = 15.0

  /**
   * 取得某個山的起始度數
   */
  override fun getStartDegree(t: Mountain): Double {
    return circleUtils.getNormalizeDegree(t.index * stepDegree + initDegree)
  }

  /**
   * 取得某個山的結束度數
   */
  override fun getEndDegree(t: Mountain): Double {
    return circleUtils.getNormalizeDegree((t.index + 1) * stepDegree + initDegree)
  }

  /** 此座山 中心點度數 */
  fun getSymbolCenter(mnt: Mountain): Double {
    return (getEndDegree(mnt) to getStartDegree(mnt)).let { (start , end) ->
      if ((end - start) > 180)
        0.0
      else
        (start + end) / 2
    }
  }

  /**
   * 取得目前這個度數位於哪個山當中
   */
  override fun get(degree: Double): Mountain {
    val index = with(circleUtils) {
      (degree.aheadOf(initDegree) / stepDegree).toInt()
    }
    return Mountain.values()[index]
  }

  abstract fun getSymbol(mnt: Mountain): Symbol
}
