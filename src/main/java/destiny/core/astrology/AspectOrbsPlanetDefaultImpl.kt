/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 12:48:16
 */
package destiny.core.astrology

import destiny.core.astrology.Aspect.*
import destiny.core.astrology.Planet.MOON
import destiny.core.astrology.Planet.SUN
import java.io.Serializable

/**
 * 「現代占星術」中，考量星體的交角容許度，內定實作 <br></br>
 * 內定只考慮日月的交角有特別的容許度 <br></br>
 * 參考資料 http://www.myastrologybook.com/aspects-and-orbs.htm
 */


class AspectOrbsPlanetDefaultImpl : IAspectOrbsPlanet, Serializable {

  /**
   * 先傳回交角容許度，再傳回門檻分數 (0~1)
   */
  override fun getPlanetAspectOrbAndThreshold(p1: Point, p2: Point, aspect: Aspect): Pair<Double, Double>? {
    return orbThresholdMap[Pair(setOf(p1 , p2) , aspect)]
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AspectOrbsPlanetDefaultImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


  companion object {

    /**
     * 兩星交角，容許度多少，以及門檻從幾分開始算起 (0~1)
     */
    private val orbThresholdMap: Map<Pair<Set<Planet>, Aspect>, Pair<Double, Double>> = mapOf(
      Pair(setOf(SUN , MOON) , CONJUNCTION) to (12.0 to 0.6),
      Pair(setOf(SUN , MOON) , OPPOSITION) to (12.0 to 0.6),
      Pair(setOf(SUN , MOON) , TRINE) to (8.0 to 0.7),
      Pair(setOf(SUN , MOON) , SQUARE) to (8.0 to 0.7),
      Pair(setOf(SUN , MOON) , SEXTILE) to (5.0 to 0.75),
      Pair(setOf(SUN , MOON) , SEMISQUARE) to (2.5 to 0.8),
      Pair(setOf(SUN , MOON) , SESQUIQUADRATE) to (2.5 to 0.8),
      Pair(setOf(SUN , MOON) , SEMISEXTILE) to (2.0 to 0.9),
      Pair(setOf(SUN , MOON) , QUINCUNX) to (2.5 to 0.9)
    )

  }



}
