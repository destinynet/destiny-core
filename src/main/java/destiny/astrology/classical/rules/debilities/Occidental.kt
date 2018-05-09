/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:13:56
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*

/**
 * Mars, Jupiter, or Saturn occidental to the Sun.
 * 火星、木星、或土星，在太陽西方
 */
class Occidental : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDegree: Double? = arrayOf(MARS , JUPITER , SATURN)
      .takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lng

    val sunDegree: Double? = h.getPosition(SUN)?.lng

    return if (sunDegree != null && planetDegree != null && IHoroscopeModel.isOccidental(planetDegree, sunDegree)) {
      "comment" to arrayOf<Any>(planet)
    } else {
      null
    }

  }
}
