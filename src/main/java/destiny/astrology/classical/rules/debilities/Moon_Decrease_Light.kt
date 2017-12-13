/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:19:43
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Moon decreasing in light.  */
class Moon_Decrease_Light : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {

    return h.getPositionOpt(planet)
      .filter { planet === Planet.MOON }.map<Double> { it.lng }.flatMap { moonDegree ->
      h.getPositionOpt(Planet.SUN)
        .map<Double> { it.lng }
        .filter { sunDegree -> Horoscope.isOriental(moonDegree!!, sunDegree!!) }
        .map { Tuple.tuple("comment", arrayOf<Any>(planet)) }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {

    val moonDegree: Double? = planet
      .takeIf { it === Planet.MOON }
      ?.let { h.getPosition(it) }?.lng

    val sunDegree: Double? = h.getPosition(Planet.SUN)?.lng

    return if (moonDegree != null && sunDegree != null && Horoscope.isOriental(moonDegree, sunDegree)) {
      "comment" to arrayOf<Any>(planet)
    } else {
      null
    }


  }
}
