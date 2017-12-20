/**
 * @author smallufo
 * Created on 2007/11/28 at 下午 3:06:17
 */
package destiny.astrology.classical

import destiny.astrology.*
import java.io.Serializable

/**
 * Essential Terms , 內定實作 , 參考 Ptolemy's Table , 以五分法
 */
class EssentialTermsDefaultImpl : IEssentialTerms, Serializable {

  override fun getTermsStar(degree: Double): Point {
    val normalizedDegree = Utils.getNormalizeDegree(degree)
    val signIndex = normalizedDegree.toInt() / 30
    (0..4)
      .map { degList[signIndex * 5 + it] }
      .filter { normalizedDegree < it.degree }
      .forEach { return it.point }
    throw RuntimeException("Cannot find Essential Terms at degree $degree , signIndex = $signIndex")
  }

  override fun getTermsStar(sign: ZodiacSign, degree: Double): Point {
    return getTermsStar(sign.degree + degree)
  }

  companion object {

    private val degList = listOf(
      //戌
      PointDegree(Planet.JUPITER, 6.0),
      PointDegree(Planet.VENUS, 14.0),
      PointDegree(Planet.MERCURY, 21.0),
      PointDegree(Planet.MARS, 26.0),
      PointDegree(Planet.SATURN, 30.0),
      //酉
      PointDegree(Planet.VENUS, 38.0),
      PointDegree(Planet.MERCURY, 45.0),
      PointDegree(Planet.JUPITER, 52.0),
      PointDegree(Planet.SATURN, 56.0),
      PointDegree(Planet.MARS, 60.0),
      //申
      PointDegree(Planet.MERCURY, 67.0),
      PointDegree(Planet.JUPITER, 74.0),
      PointDegree(Planet.VENUS, 81.0),
      PointDegree(Planet.SATURN, 85.0),
      PointDegree(Planet.MARS, 90.0),
      //未
      PointDegree(Planet.MARS, 96.0),
      PointDegree(Planet.JUPITER, 103.0),
      PointDegree(Planet.MERCURY, 110.0),
      PointDegree(Planet.VENUS, 117.0),
      PointDegree(Planet.SATURN, 120.0),
      //午
      PointDegree(Planet.SATURN, 126.0),
      PointDegree(Planet.MERCURY, 133.0),
      PointDegree(Planet.VENUS, 139.0),
      PointDegree(Planet.JUPITER, 145.0),
      PointDegree(Planet.MARS, 150.0),
      //巳
      PointDegree(Planet.MERCURY, 157.0),
      PointDegree(Planet.VENUS, 163.0),
      PointDegree(Planet.JUPITER, 168.0),
      PointDegree(Planet.SATURN, 174.0),
      PointDegree(Planet.MARS, 180.0),
      //辰
      PointDegree(Planet.SATURN, 186.0),
      PointDegree(Planet.VENUS, 191.0),
      PointDegree(Planet.JUPITER, 199.0),
      PointDegree(Planet.MERCURY, 204.0),
      PointDegree(Planet.MARS, 210.0),
      //卯
      PointDegree(Planet.MARS, 216.0),
      PointDegree(Planet.JUPITER, 224.0),
      PointDegree(Planet.VENUS, 231.0),
      PointDegree(Planet.MERCURY, 237.0),
      PointDegree(Planet.SATURN, 240.0),
      //寅
      PointDegree(Planet.JUPITER, 248.0),
      PointDegree(Planet.VENUS, 254.0),
      PointDegree(Planet.MERCURY, 259.0),
      PointDegree(Planet.SATURN, 265.0),
      PointDegree(Planet.MARS, 270.0),
      //丑
      PointDegree(Planet.VENUS, 276.0),
      PointDegree(Planet.MERCURY, 282.0),
      PointDegree(Planet.JUPITER, 289.0),
      PointDegree(Planet.MARS, 295.0),
      PointDegree(Planet.SATURN, 300.0),
      //子
      PointDegree(Planet.SATURN, 306.0),
      PointDegree(Planet.MERCURY, 312.0),
      PointDegree(Planet.VENUS, 320.0),
      PointDegree(Planet.JUPITER, 325.0),
      PointDegree(Planet.MARS, 330.0),
      //亥
      PointDegree(Planet.VENUS, 338.0),
      PointDegree(Planet.JUPITER, 344.0),
      PointDegree(Planet.MERCURY, 350.0),
      PointDegree(Planet.MARS, 356.0),
      PointDegree(Planet.SATURN, 359.999999999999) //如果改成 360 , 會被 normalize 成 0
    )
  }
}

