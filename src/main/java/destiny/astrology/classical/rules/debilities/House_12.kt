/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:00:33
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

class House_12 : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { it == 12 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
