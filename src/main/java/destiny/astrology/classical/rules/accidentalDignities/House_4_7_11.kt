/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:44:17
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

/** In the 7th, 4th, or 11th (Good Daemon's) houses.  */
class House_4_7_11 : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { intArrayOf(4, 7, 11).contains(it) }
      ?.let { house -> Pair("comment", arrayOf(planet, house)) }
  }
}
