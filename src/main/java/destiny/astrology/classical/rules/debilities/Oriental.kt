/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:16:57
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*

/**
 * Mercury, or Venus oriental to the Sun.
 * 金星、水星，是否 東出 於 太陽
 */
class Oriental : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDegree: Double? = arrayOf(MERCURY , VENUS).takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lng

    val sunDegree: Double? = h.getPosition(SUN)?.lng

    return if (sunDegree != null && planetDegree != null && IHoroscopeModel.isOriental(planetDegree, sunDegree)) {
      "comment" to arrayOf<Any>(planet)
    } else {
      null
    }
  }
}
