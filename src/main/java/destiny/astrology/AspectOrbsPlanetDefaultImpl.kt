/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 12:48:16
 */
package destiny.astrology

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

  companion object {

    /**
     * 兩星交角，容許度多少，以及門檻從幾分開始算起 (0~1)
     */
    private val orbThresholdMap: Map<Pair<Set<Planet>, Aspect>, Pair<Double, Double>> = mapOf(
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.CONJUNCTION) to (12.0 to 0.6),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.OPPOSITION) to (12.0 to 0.6),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.TRINE) to (8.0 to 0.7),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.SQUARE) to (8.0 to 0.7),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.SEXTILE) to (5.0 to 0.75),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.SEMISQUARE) to (2.5 to 0.8),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.SESQUIQUADRATE) to (2.5 to 0.8),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.SEMISEXTILE) to (2.0 to 0.9),
      Pair(setOf(Planet.SUN , Planet.MOON) , Aspect.QUINCUNX) to (2.5 to 0.9)
    )

  }



}
