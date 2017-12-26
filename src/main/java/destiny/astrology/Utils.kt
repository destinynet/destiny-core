/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 4:26:17
 */
package destiny.astrology

object Utils {

  /** 將度數 normalize 到 0(含)~360(不含) 的區間  */
  fun getNormalizeDegree(degree: Double): Double {
    return when {
      degree >= 360 -> degree % 360
      degree < 0 -> (360 - (0 - degree) % 360) % 360
      else -> degree
    }
  }


}
