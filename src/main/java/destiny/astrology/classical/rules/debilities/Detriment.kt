/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:46:40
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity

/** In Detriment.  */
class Detriment : EssentialRule() {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)
      ?.takeIf { sign -> planet === essentialImpl.getPoint(sign, Dignity.DETRIMENT) }
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
