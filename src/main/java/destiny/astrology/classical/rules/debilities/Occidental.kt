/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:13:56
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * Mars, Jupiter, or Saturn occidental to the Sun.
 * 火星、木星、或土星，在太陽西方
 */
class Occidental : Rule() {

  public override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDegree: Double? = arrayOf(MARS , JUPITER , SATURN)
      .takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lng

    val sunDegree: Double? = h.getPosition(SUN)?.lng

    return if (sunDegree != null && planetDegree != null && Horoscope.isOccidental(planetDegree , sunDegree)) {
      "comment" to arrayOf<Any>(planet)
    } else {
      null
    }

  }
}
