/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:05:56
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

class Retrograde : DebilityRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getStarPosition(planet)
      ?.speedLng
      ?.takeIf { it < 0 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
