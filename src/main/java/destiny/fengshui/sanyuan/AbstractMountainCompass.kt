/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.fengshui.sanyuan

import destiny.astrology.Utils
import destiny.fengshui.ICompass

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
    return Utils.getNormalizeDegree(t.index * stepDegree + initDegree)
  }

  /**
   * 取得某個山的結束度數
   */
  override fun getEndDegree(t: Mountain): Double {
    return Utils.getNormalizeDegree((t.index + 1) * stepDegree + initDegree)
  }

  /**
   * 取得目前這個度數位於哪個山當中
   */
  fun getMnt(degree: Double): Mountain {
    var index = ((degree + 360 - initDegree) / stepDegree).toInt()
    if (index >= 24)
      index -= 24
    else if (index < 0)
      index += 24
    return Mountain.values()[index]
  }



}
