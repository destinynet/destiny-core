/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:22:25
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Combust the Sun (between 17' and 8.5 from Sol).  */
class Combustion : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    if (planet !== Planet.SUN) {
      if (h.getAngle(planet, Planet.SUN) > 17.0 / 60 && h.getAngle(planet, Planet.SUN) <= 8.5) {
        //addComment(Locale.TAIWAN , planet + " 被太陽焦傷 (Combustion)");
        return Optional.of(Tuple.tuple("comment", arrayOf<Any>(planet)))
      }
    }
    return Optional.empty()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== Planet.SUN }
      .takeIf { h.getAngle(planet , Planet.SUN) > 17.0 / 60.0 && h.getAngle(planet , Planet.SUN) <= 8.5 }
      .let { "comment" to arrayOf<Any>(planet) }
  }
}
