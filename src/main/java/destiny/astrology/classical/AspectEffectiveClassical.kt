/**
 * @author smallufo
 * Created on 2007/11/22 at 下午 10:42:18
 */
package destiny.astrology.classical

import destiny.astrology.Aspect
import destiny.astrology.IAspectEffective
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Point
import java.io.Serializable
import kotlin.math.abs

/**
 * <pre>
 * 「古典占星」的交角判定，古典占星只計算「星體光芒的容許度」與「交角」是否有效，並沒有考慮交角的類型。
 * （「現代占星」則是各種交角都有不同的容許度）
 * 演算法採用 Template Method design pattern
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 未來可以繼承此 Abstract Class , 呼叫資料庫 , 取得個人化的 OrbsMap
 *
 * @param planetOrbsImpl 星芒交角 , 內定採用 [PointDiameterAlBiruniImpl]
 */
class AspectEffectiveClassical(val planetOrbsImpl: IPointDiameter = PointDiameterAlBiruniImpl()) : IAspectEffective, Serializable {


  private fun getAngleDiff(deg1: Double , deg2: Double , angle: Double) : Double {
    return abs(IHoroscopeModel.getAngle(deg1, deg2) - angle)
  }

  private fun getSumOfRadius(p1: Point , p2: Point) : Double {
    return (planetOrbsImpl.getDiameter(p1) + planetOrbsImpl.getDiameter(p2)) / 2
  }

  override fun isEffectiveAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Pair<Boolean , Double> {

    val angleDiff = getAngleDiff(deg1, deg2, aspect.degree)
    val sumOfRadius = getSumOfRadius(p1 , p2)

    return (angleDiff <= sumOfRadius).let { value ->
      if (value)
        true to (0.6 + 0.4 * (sumOfRadius - angleDiff) / sumOfRadius)
      else
        false to 0.0
    }
  }


  /**
   * classical 交角不談容許度 (orb)
   */
  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, angle: Double): Boolean {
    val angleDiff = getAngleDiff(deg1, deg2, angle)
    val sumOfRadius = getSumOfRadius(p1 , p2)
    return (angleDiff <= sumOfRadius)
  }

  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道上的度數
   * @param angles 判定的角度，例如 [0,60,90,120,180]
   * @return 兩顆星是否形成有效交角
   */
  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, vararg angles: Double): Boolean {
    return angles.any {
      (isEffective(p1, deg1, p2, deg2, it))
    }
  }

  /** 兩星體是否形成有效交角  */
  override fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Boolean {
    return isEffective(p1, deg1, p2, deg2, aspect.degree)
  }

}
