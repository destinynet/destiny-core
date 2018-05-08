/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:24:40
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoro
import destiny.astrology.Planet

/** Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !?  */
class Free_Combustion : Rule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== Planet.SUN }
      ?.takeIf { h.getAngle(it , Planet.SUN) > 17 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
