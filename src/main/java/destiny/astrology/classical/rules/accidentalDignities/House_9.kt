/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:48:01
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** In the 9th house.  */
class House_9 : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getHouseOpt(planet)
      .filter { house -> house == 9 }
      .map { house -> Tuple.tuple("comment", arrayOf(planet, house)) }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getHouse(planet)?.takeIf { it == 9 }?.let { house ->
      Pair("comment", arrayOf(planet, house))
    }
  }
}
