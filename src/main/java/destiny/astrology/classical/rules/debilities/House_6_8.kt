/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:02:49
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoro
import destiny.astrology.Planet

class House_6_8 : Rule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { it == 6 || it == 8 }
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
