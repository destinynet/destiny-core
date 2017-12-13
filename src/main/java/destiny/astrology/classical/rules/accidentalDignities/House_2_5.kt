/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:46:56
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** In the 2nd or 5th house.  */
class House_2_5 : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getHouseOpt(planet)
      .filter { house -> house == 2 || house == 5 }
      .map { house -> Tuple.tuple("comment", arrayOf(planet, house)) }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getHouse(planet)?.takeIf { it == 2 || it == 5 }?.let { house ->
      Pair("comment", arrayOf(planet, house))
    }
  }
}
