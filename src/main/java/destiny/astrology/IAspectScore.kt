/**
 * Created by smallufo on 2019-05-19.
 */
package destiny.astrology

import destiny.astrology.classical.IPointDiameter
import destiny.astrology.classical.PointDiameterAlBiruniImpl
import java.io.Serializable
import kotlin.math.abs

interface IAspectScore {
  fun isEffectiveAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Pair<Boolean , Double>
}

class AspectScoreClassicalDefaultImpl(private val planetOrbsImpl: IPointDiameter = PointDiameterAlBiruniImpl()) : IAspectScore , Serializable {

  private fun getAngleDiff(deg1: Double , deg2: Double , angle: Double) : Double {
    return abs(IHoroscopeModel.getAngle(deg1, deg2) - angle)
  }

  private fun getSumOfRadius(p1: Point , p2: Point) : Double {
    return (planetOrbsImpl.getDiameter(p1) + planetOrbsImpl.getDiameter(p2)) / 2
  }

  override fun isEffectiveAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Pair<Boolean, Double> {
    val angleDiff = getAngleDiff(deg1, deg2, aspect.degree)
    val sumOfRadius = getSumOfRadius(p1 , p2)

    return (angleDiff <= sumOfRadius).let { value ->
      if (value)
        true to (0.6 + 0.4 * (sumOfRadius - angleDiff) / sumOfRadius)
      else
        false to 0.0
    }
  }
}

class AspectScoreModernDefaultImpl(/** 「不考慮行星」的交角容許度 */
                                   var aspectOrbsImpl: IAspectOrbs = AspectOrbsDefaultImpl(),
                                   /** 「考量行星的」交角容許度實作 */
                                   var aspectOrbsPlanetImpl: IAspectOrbsPlanet = AspectOrbsPlanetDefaultImpl()) : IAspectScore , Serializable {

  /** 兩星交角容許度是多少 */
  private fun getOrb(p1: Point, p2: Point, aspect: Aspect): Double {
    //從「考量行星」的交角容許度實作找起
    return aspectOrbsPlanetImpl.getPlanetAspectOrb(p1, p2, aspect)
      ?: aspectOrbsImpl.getAspectOrb(aspect) // 再從「不考慮行星」的交角容許度尋找
  }

  override fun isEffectiveAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Pair<Boolean, Double> {
    val orb = getOrb(p1, p2, aspect)
    val angle = IHoroscopeModel.getAngle(deg1, deg2)
    val angleDiff = abs(angle - aspect.degree)

    return (angleDiff <= orb).let { value ->
      if (value) {
        true to (0.6 + 0.4 * (orb - angleDiff) / orb)
      } else {
        false to 0.0
      }
    }
  }

}