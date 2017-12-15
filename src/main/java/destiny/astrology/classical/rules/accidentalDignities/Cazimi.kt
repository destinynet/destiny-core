/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:26:11
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Cazimi (within 17 minutes of the Sun).  */
class Cazimi : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }


  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== Planet.SUN }
      ?.takeIf { h.getAngle(it , Planet.SUN) < 17.0/60.0 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
