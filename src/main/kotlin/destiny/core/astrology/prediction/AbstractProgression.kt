/**
 * @author smallufo
 * Created on 2008/4/5 改寫
 */
package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay
import destiny.tools.JSerializable

/**
 * Progression 抽象類別，具備 Progression 演算法的 template methods
 */
abstract class AbstractProgression : ILinear, Conversable, JSerializable {

  abstract val type: ProgressionType

  /** 是否順推，內定是順推  */
  override val forward = true

  /** Numerator: 分子 , 假設以 [ProgressionSecondary] (一日一年)來說 , 分子是一年(有幾秒)  */
  protected abstract val numerator: Double

  /** Denominator: 分母 , 假設以 [ProgressionSecondary] (一日一年)來說 , 分母是一日(有幾秒)  */
  protected abstract val denominator: Double

  /**
   * 「收斂」到的時間，是 [getDivergentTime] 的反函數。
   *
   * 帶正負號：nowTime 早於出生時，收斂時間也落在出生之前，映射保持單調（from < to 映射後仍是 from < to）。
   */
  override fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    val durDays = (nowGmtJulDay - natalGmtJulDay) / numerator * denominator

    return if (!forward) {
      natalGmtJulDay - durDays
    } else {
      natalGmtJulDay + durDays
    }
  }


  /**
   * 實作 [ILinear]
   * 計算從 nowTime 相對於 natalTime , 「發散(diverge)」到(未來的)哪個時間
   */
  override fun getDivergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {

    val diffDays = (nowGmtJulDay - natalGmtJulDay) / denominator * numerator

    return if (!forward) {
      natalGmtJulDay - diffDays
    } else {
      natalGmtJulDay + diffDays
    }
  }

}
