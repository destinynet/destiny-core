/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:26:11
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

/** Cazimi (within 17 minutes of the Sun).  */
class Cazimi : AccidentalRule() {


  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== Planet.SUN }
      ?.takeIf { h.getAngle(it , Planet.SUN) < 17.0/60.0 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
