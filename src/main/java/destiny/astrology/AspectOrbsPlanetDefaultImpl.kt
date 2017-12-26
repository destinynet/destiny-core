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
   * @param aspect 欲取得容許度之交角
   * @return 交角容許度，如果傳回 小於零，代表找不到其值
   */
  override fun getPlanetAspectOrb(p1: Point, p2: Point, aspect: Aspect): Double? {

    val key1 = Triple(p1 , p2 , aspect)
    val key2 = Triple(p2 , p1 , aspect)

    return map[key1]?:map[key2]
  }

  companion object {
    private val map = mapOf(
      Triple(Planet.SUN, Planet.MOON, Aspect.CONJUNCTION) to 12.0,
      Triple(Planet.SUN, Planet.MOON, Aspect.OPPOSITION) to 12.0,
      Triple(Planet.SUN, Planet.MOON, Aspect.TRINE) to 8.0,
      Triple(Planet.SUN, Planet.MOON, Aspect.SQUARE) to 8.0,
      Triple(Planet.SUN, Planet.MOON, Aspect.SEXTILE) to 5.0,
      Triple(Planet.SUN, Planet.MOON, Aspect.SEMISQUARE) to 2.5,
      Triple(Planet.SUN, Planet.MOON, Aspect.SESQUIQUADRATE) to 2.5,
      Triple(Planet.SUN, Planet.MOON, Aspect.SEMISEXTILE) to 2.0,
      Triple(Planet.SUN, Planet.MOON, Aspect.QUINCUNX) to 2.5
    )
  }

}
