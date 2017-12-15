/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:56:41
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Aspect
import destiny.astrology.AspectEffectiveModern
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.JUPITER
import destiny.astrology.Planet.VENUS
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile aspect Jupiter or Venus.  */
class Partile_Sextile_Jupiter_Venus : Rule() {

  private val aspect = Aspect.SEXTILE

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val jupiterDeg = h.getPosition(JUPITER)?.lng
    val venusDeg = h.getPosition(VENUS)?.lng

    return planetDeg?.let {
      val jupResult = jupiterDeg?.takeIf {
        planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, aspect, 1.0)
      }?.let { "comment" to arrayOf(planet, JUPITER, aspect) }

      val venResult = venusDeg?.takeIf {
        planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, aspect, 1.0)
      }?.let { "comment" to arrayOf(planet, VENUS, aspect) }

      if (jupResult != null)
        return@let jupResult
      else
        return@let venResult
    }
  }
}
