/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:07:11
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * 喜樂宮 Joy House.
 * Mercory in 1st.
 * Moon in 3rd.
 * Venus in 5th.
 * Mars in 6th.
 * Sun in 9th.
 * Jupiter in 11th.
 * Saturn in 12th.
 */
class JoyHouse : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {

    return h.getHouseOpt(planet)
      .filter { house ->
        planet === MERCURY && house == 1 ||
          planet === MOON && house == 3 ||
          planet === VENUS && house == 5 ||
          planet === MARS && house == 6 ||
          planet === SUN && house == 9 ||
          planet === JUPITER && house == 11 ||
          planet === SATURN && house == 12
      }
      .map { house -> Tuple.tuple("comment", arrayOf(planet, house)) }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getHouse(planet)?.takeIf { house ->
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
