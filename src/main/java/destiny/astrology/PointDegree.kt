/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 10:34:07
 */
package destiny.astrology

import java.io.Serializable
import java.util.*

/**
 * 純粹資料結構，存放星體(日月/行星/南北交點/恆星)在黃道帶上的度數 (0~360)
 */
class PointDegree : Serializable {

  val point: Point

  val degree: Double

  /** 取得黃道此度數所在的星座  */
  val zodiacSign: ZodiacSign
    get() = ZodiacSign.of(degree)

  /** 此星體在黃道帶上幾度  */
  constructor(p: Point, deg: Double) {
    this.point = p
    this.degree = Utils.getNormalizeDegree(deg)
  }

  /** 此星體在某星座幾度 , deg 必須小於 30 , 否則丟出 RuntimeException  */
  constructor(s: Point, sign: ZodiacSign, deg: Double) {
    if (deg < 0 || deg >= 30)
      throw RuntimeException("deg must between 0(inclusive) and 30(exclusive). ")
    
    this.point = s
    this.degree = sign.degree + deg
  }

  /** 取得此度數對於此星座，是幾度  */
  fun getDegreeOf(sign: ZodiacSign): Double {
    return degree - sign.degree
  }

  override fun toString(): String {
    return point.toString() + "/" + degree
  }


  override fun equals(o: Any?): Boolean {
    if (this === o)
      return true
    if (o !is PointDegree)
      return false
    val that = o as PointDegree?
    return java.lang.Double.compare(that!!.degree, degree) == 0 && point == that.point
  }

  override fun hashCode(): Int {
    return Objects.hash(point, degree)
  }
}
