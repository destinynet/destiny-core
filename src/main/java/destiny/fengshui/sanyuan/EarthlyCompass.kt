/**
 * @author smallufo
 * @date 2002/9/23
 * @time 上午 11:19:48
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import kotlin.math.abs


/**
 * 正針24山(地盤) , 子山從 352.5 度開始
 */
class EarthlyCompass : AbstractMountainCompass() {

  override val initDegree: Double
    get() = 352.5 // 360-7.5

  /** 此座山 是位於哪一卦中 */
  fun getSymbol(mnt: Mountain): Symbol {
    //詢問此山/向 的中心點度數為：
    val midMountain: Double = if (abs(getEndDegree(mnt) - getStartDegree(mnt)) > 180)
      0.0
    else
      (getEndDegree(mnt) + getStartDegree(mnt)) / 2

    return 後天八卦盤.getSymbol(midMountain)
  }

  companion object {
    val 後天八卦盤 = AcquiredSymbolCompass()
  }
}
