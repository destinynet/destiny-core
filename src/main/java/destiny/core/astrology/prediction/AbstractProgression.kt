/**
 * @author smallufo
 * Created on 2008/4/5 改寫
 */
package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay
import java.io.Serializable
import kotlin.math.absoluteValue

/**
 * Progression 抽象類別，具備 Progression 演算法的 template methods
 */
abstract class AbstractProgression : ILinear, Conversable, Serializable {

  abstract val type: ProgressionType

  /** 是否逆推，內定是順推  */
  override val converse = false

  /** Numerator: 分子 , 假設以 [ProgressionSecondary] (一日一年)來說 , 分子是一年(有幾秒)  */
  protected abstract val numerator: Double

  /** Denominator: 分母 , 假設以 [ProgressionSecondary] (一日一年)來說 , 分母是一日(有幾秒)  */
  protected abstract val denominator: Double

  /** 「收斂」到的時間 */
  override fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    val dur = (natalGmtJulDay - nowGmtJulDay).absoluteValue

    val durDays = dur / numerator * denominator

    return if (converse) {
      natalGmtJulDay - durDays
    } else {
      natalGmtJulDay + durDays
    }
  }


  /**
   * 實作 [ILinear]
   * 計算從 nowTime 相對於 natalTime , 「發散(diverge)」到(未來的)哪個時間
   */
  override fun getDivergentTime(natalGmtJulDay: GmtJulDay, nowTime: GmtJulDay): GmtJulDay {

    val diffDays = (nowTime - natalGmtJulDay) / denominator * numerator

    return if (converse) {
      natalGmtJulDay - diffDays
    } else {
      natalGmtJulDay + diffDays
    }
  }

}
