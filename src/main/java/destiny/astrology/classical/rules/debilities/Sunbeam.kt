/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:25:34
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.SUN
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Under the Sunbeams (between 8.5 and 17 from Sol).  */
class Sunbeam : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    if (planet !== SUN) {
      if (h.getAngle(planet, SUN) > 8.5 && h.getAngle(planet, SUN) <= 17) {
        //addComment(Locale.TAIWAN , planet + " 被太陽曬傷 (Sunbeam)");
        return Optional.of(Tuple.tuple("comment", arrayOf<Any>(planet)))
      }
    }
    return Optional.empty()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== SUN }
      ?.takeIf { h.getAngle(it , SUN) > 8.5 && h.getAngle(it , SUN) <= 17.0 }
      ?.let { "comment" to arrayOf<Any>(planet)}
  }
}
