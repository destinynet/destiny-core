/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:24:40
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !?  */
class Free_Combustion : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== Planet.SUN }
      ?.takeIf { h.getAngle(it , Planet.SUN) > 17 }
      ?.let { "comment" to arrayOf<Any>(planet) }
  }
}
