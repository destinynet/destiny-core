/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:07:11
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*

/**
 * 喜樂宮 Joy House.
 * Mercury in 1st.
 * Moon in 3rd.
 * Venus in 5th.
 * Mars in 6th.
 * Sun in 9th.
 * Jupiter in 11th.
 * Saturn in 12th.
 */
class JoyHouse : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { house ->
        planet === MERCURY && house == 1 ||
          planet === MOON && house == 3 ||
          planet === VENUS && house == 5 ||
          planet === MARS && house == 6 ||
          planet === SUN && house == 9 ||
          planet === JUPITER && house == 11 ||
          planet === SATURN && house == 12
      }?.let { house -> "comment" to arrayOf(planet, house) }
  }
}
