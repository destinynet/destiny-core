/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:46:40
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoro
import destiny.astrology.Planet

/** In Detriment.  */
class Detriment : EssentialRule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {

    return h.getZodiacSign(planet)
      ?.takeIf { sign -> planet === detrimentImpl.getPoint(sign) }
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
