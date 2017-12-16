/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:46:56
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet

/** In the 2nd or 5th house.  */
class House_2_5 : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { it == 2 || it == 5 }
      ?.let { house -> Pair("comment", arrayOf(planet, house)) }
  }
}
