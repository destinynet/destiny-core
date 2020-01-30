/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 4:26:17
 */
package destiny.tools

object circleUtils {

  /** 將度數 normalize 到 0(含)~360(不含) 的區間  */
  private fun Double.normalize() : Double {
    return when {
      this >= 360 -> this % 360
      this < 0 -> (360 - (0 - this) % 360) % 360
      else -> this
    }
  }

  /** 將度數 normalize 到 0(含)~360(不含) 的區間  */
  fun getNormalizeDegree(degree: Double): Double {
    return degree.normalize()
  }

  /**
   * 某度數 在 圓環上 領先另一度數 幾度
   * 例如
   * 1.aheadOf(0) = 0
   * 359.aheadOf(0) = 359
   * 0.aheadOf(359) = 1
   */
  fun Double.aheadOf(degree: Double) : Double {
    return (this.normalize() - degree.normalize()).let { value ->
      if (value < 0) {
        (value + 360)
      } else {
        value
      }
    }
  }


}
