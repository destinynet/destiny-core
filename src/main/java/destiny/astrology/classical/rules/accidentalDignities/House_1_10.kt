/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:39:42
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

/** In the 10th or 1st house.  */
class House_1_10 : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { it == 1 || it == 10 }
      ?.let { house -> Pair("comment", arrayOf(planet, house)) }
  }
}
