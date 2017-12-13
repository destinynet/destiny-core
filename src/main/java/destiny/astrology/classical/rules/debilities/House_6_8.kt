/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:02:49
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

class House_6_8 : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getHouseOpt(planet)
      .filter { house -> house == 6 || house == 8 }
      .map { house -> Tuple.tuple("comment", arrayOf(planet, house)) }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getHouse(planet)?.takeIf { it == 6 || it == 8 }
      ?.let { "comment" to arrayOf(planet , it) }
  }
}
