/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:50:05
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.IStarPositionWithAzimuth
import destiny.astrology.Planet
import destiny.astrology.Planet.MOON
import destiny.astrology.Planet.SUN

/** Direct in motion (does not apply to Sun and Moon).  */
class Direct : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== SUN && it !== MOON }
      ?.let { h.getStarPosition(it) }
      ?.speedLng
      ?.takeIf { it > 0 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
