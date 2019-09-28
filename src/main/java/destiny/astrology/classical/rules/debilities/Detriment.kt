/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:46:40
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

/** In Detriment.  */
class Detriment : EssentialRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {

    return h.getZodiacSign(planet)
      ?.takeIf { sign -> planet === with(detrimentImpl) { sign.getDetrimentPoint() } }
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
