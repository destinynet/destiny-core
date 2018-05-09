/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:51:31
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet

/** In Fall.  */
class Fall : EssentialRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)
      ?.takeIf { sign -> planet === fallImpl.getPoint(sign)}
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
