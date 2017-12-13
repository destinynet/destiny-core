/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:16:57
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * Mercury, or Venus oriental to the Sun.
 * 金星、水星，是否 東出 於 太陽
 */
class Oriental : Rule() {

  public override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {

    return h.getPositionOpt(planet)
      .filter { planet === MERCURY || planet === VENUS }
      .map<Double> { it.lng }.flatMap { planetDegree ->
      h.getPositionOpt(SUN)
        .map<Double> { it.lng }
        .filter { sunDegree -> Horoscope.isOriental(planetDegree!!, sunDegree!!) }
        .map { Tuple.tuple("comment", arrayOf<Any>(planet)) }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDegree: Double? = arrayOf(MERCURY , VENUS).takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lng

    val sunDegree: Double? = h.getPosition(SUN)?.lng

    return if (sunDegree != null && planetDegree != null && Horoscope.isOriental(planetDegree, sunDegree)) {
      "comment" to arrayOf<Any>(planet)
    } else {
      null
    }
  }
}
