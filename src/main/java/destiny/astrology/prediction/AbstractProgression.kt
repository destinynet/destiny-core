/**
 * @author smallufo
 * Created on 2008/4/5 改寫
 */
package destiny.astrology.prediction

import java.io.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Progression 抽象類別，具備 Progression 演算法的 template methods
 */
abstract class AbstractProgression : ILinear, Conversable, Serializable {
  /** 是否逆推，內定是順推  */
  override val converse = false

  /** Numerator: 分子 , 假設以 SecondaryProgression (一日一年)來說 , 分子是一年(有幾秒)  */
  protected abstract val numerator: Double

  /** Denominator: 分母 , 假設以 SecondaryProgression (一日一年)來說 , 分母是一日(有幾秒)  */
  protected abstract val denominator: Double


  /**
   * 實作 Mappable
   * Template Method , 計算 nowTime 相對於 natalTime , 「收斂(converge)」到的時間<br></br>
   * 不限定是 GMT 或是 LMT , 但兩者要一樣的時區
   */
  override fun getConvergentTime(natalTime: ChronoLocalDateTime<*>, nowTime: ChronoLocalDateTime<*>): ChronoLocalDateTime<*> {
    val dur = Duration.between(natalTime, nowTime).abs()


    val secsDouble = dur.seconds / numerator * denominator
    val secs = secsDouble.toLong()
    val nanos = ((secsDouble - secs) * 1000000000).toLong()

    return if (converse)
      natalTime.minus(secs, ChronoUnit.SECONDS).minus(nanos, ChronoUnit.NANOS)
    else
      natalTime.plus(secs, ChronoUnit.SECONDS).plus(nanos, ChronoUnit.NANOS)
  }


  /**
   * 實作 LinearIF
   * Template Method , 計算從 nowTime 相對於 natalTime , 「發散(diverge)」到(未來的)哪個時間<br></br>
   * 不限定是 GMT 或是 LMT , 但兩者要一樣的時區
   */
  override fun getDivergentTime(natalTime: ChronoLocalDateTime<*>, nowTime: ChronoLocalDateTime<*>): ChronoLocalDateTime<*> {
    val dur = Duration.between(natalTime, nowTime)
    val diffSeconds = dur.seconds

    val secDouble = diffSeconds / denominator * numerator
    val secs = secDouble.toLong()
    val nanos = ((secDouble - secs) * 1000000000).toLong()

    return if (converse) {
      natalTime.minus(secs, ChronoUnit.SECONDS).minus(nanos, ChronoUnit.NANOS)
    } else {
      natalTime.plus(secs, ChronoUnit.SECONDS).plus(nanos, ChronoUnit.NANOS)
    }
  }


}
