/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 10:34:07
 */
package destiny.astrology

import java.io.Serializable

/**
 * 純粹資料結構，存放星體(日月/行星/南北交點/恆星)在黃道帶上的度數 (0~360)
 */
data class PointDegree(val point: Point, val degree: Double) : Serializable {

  /** 取得黃道此度數所在的星座  */
  val zodiacSign: ZodiacSign
    get() = ZodiacSign.of(degree)

  /** 此星體在某星座幾度 , deg 必須小於 30 , 否則丟出 RuntimeException  */
  constructor(s: Point, sign: ZodiacSign, deg: Double) : this(s, sign.degree + deg) {
    if (deg < 0 || deg >= 30)
      throw RuntimeException("deg must between 0(inclusive) and 30(exclusive). ")
  }

  /** 取得此度數對於此星座，是幾度  */
  fun getDegreeOf(sign: ZodiacSign): Double {
    return degree - sign.degree
  }

  override fun toString(): String {
    return "$point/$degree"
  }
}
