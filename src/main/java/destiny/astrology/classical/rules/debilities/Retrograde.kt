/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:05:56
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

class Retrograde : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getPositionOpt(planet)
      .map<Double>({ it.speedLng })
      .filter { speedLng -> speedLng < 0 }
      .map { Tuple.tuple("comment", arrayOf<Any>(planet)) }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getPosition(planet)?.speedLng?.takeIf { it < 0 }?.let { "comment" to arrayOf<Any>(planet) }
  }
}
