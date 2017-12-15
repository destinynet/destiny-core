/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 5:10:06
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Mars, Jupiter, or Saturn oriental of (rising before) the Sun.
 * 火星、木星、土星 是否 東出 於 太陽
 */
class Oriental : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDegree: Double? = arrayOf(MARS, JUPITER, SATURN)
      .takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lng

    val sunDegree: Double? = h.getPosition(SUN)?.lng

    return if (sunDegree != null && planetDegree != null && Horoscope.isOriental(planetDegree, sunDegree)) {
      "comment" to arrayOf<Any>(planet)
    } else {
      null
    }
  }
}
