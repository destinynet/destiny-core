/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:22:25
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoro
import destiny.astrology.Planet

/** Combust the Sun (between 17' and 8.5 from Sol).  */
class Combustion : Rule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== Planet.SUN }
      ?.takeIf { h.getAngle(planet , Planet.SUN) > 17.0 / 60.0 && h.getAngle(planet , Planet.SUN) <= 8.5 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
