/**
 * Created by smallufo on 2019-11-17.
 */
package destiny.fengshui

import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.congenital
import java.io.Serializable

/**
 * 先天64卦羅盤 , for 伏羲先天六十四卦天圓地方圖 [destiny.iching.congenital]
 */
class HexagramCongenitalCompass : ICompass<IHexagram>, Serializable {

  override val initDegree: Double = 0.0

  override val stepDegree: Double = 5.625 // 360 / 64.0

  override fun getStartDegree(t: IHexagram): Double {
    return with(congenital.circle) {
      t.aheadOf(Hexagram.復) * stepDegree
    }
  }

  override fun getEndDegree(t: IHexagram): Double {
    return getStartDegree(t) + stepDegree
  }

  /**
   * 此度 是在哪個卦中
   */
  override fun get(degree: Double): IHexagram {
    val steps : Int = (degree / stepDegree).toInt()
    return with(congenital.circle) {
      Hexagram.復.next(steps)
    }
  }


}
