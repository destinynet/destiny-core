/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:49:19
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** In the 3rd house.  */
class House_3 : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet, h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getHouse(planet)
      ?.takeIf { it == 3 }
      ?.let { house -> Pair("comment", arrayOf(planet, house)) }
  }
}
