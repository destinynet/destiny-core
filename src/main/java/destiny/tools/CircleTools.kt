/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 4:26:17
 */
package destiny.tools

import kotlin.math.abs

object CircleTools {

  /** 將度數 normalize 到 0(含)~360(不含) 的區間  */
  fun Double.normalize(): Double {
    return when {
      this >= 360 -> this % 360
      this < 0    -> (360 - (0 - this) % 360) % 360
      else        -> this
    }
  }

  /** 將度數 normalize 到 0(含)~360(不含) 的區間  */
  fun getNormalizeDegree(degree: Double): Double {
    return degree.normalize()
  }

  /** 中間度數 */
  fun getCenterDegree(from: Double, to: Double): Double {
    return getNormalizeDegree((from + to) / 2).let { c ->
      // 避免右方的 0 度 flip 到左方
      if (abs(from - to) >= 180) {
        getNormalizeDegree(c - 180)
      } else {
        c
      }
    }
  }

  /**
   * 某度數 在 圓環上 領先另一度數 幾度
   * 例如
   * 1.aheadOf(0) = 0
   * 359.aheadOf(0) = 359
   * 0.aheadOf(359) = 1
   */
  fun Double.aheadOf(degree: Double): Double {
    return (this.normalize() - degree.normalize()).let { value ->
      if (value < 0) {
        (value + 360)
      } else {
        value
      }
    }
  }


}
