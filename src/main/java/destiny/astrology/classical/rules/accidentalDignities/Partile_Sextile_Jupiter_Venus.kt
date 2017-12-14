/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:56:41
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Aspect.SEXTILE
import destiny.astrology.AspectEffectiveModern
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.JUPITER
import destiny.astrology.Planet.VENUS
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile sextile Jupiter or Venus.  */
class Partile_Sextile_Jupiter_Venus : Rule() {


  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {

    return h.getPositionOpt(planet).map<Double> { it.lng }.flatMap { planetDegree ->
      h.getPositionOpt(JUPITER).map<Double> { it.lng }.flatMap { jupiterDeg ->
        h.getPositionOpt(VENUS).map<Double> { it.lng }.flatMap { venusDeg ->
          if (planet !== JUPITER && AspectEffectiveModern.isEffective(planetDegree!!, jupiterDeg!!, SEXTILE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, JUPITER, SEXTILE)
            Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, JUPITER, SEXTILE)))
          } else if (planet !== VENUS && AspectEffectiveModern.isEffective(planetDegree!!, venusDeg!!, SEXTILE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, VENUS, SEXTILE)
            Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, VENUS, SEXTILE)))
          }
          Optional.empty<Tuple2<String, Array<Any>>>()
        }
      }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val jupiterDeg = h.getPosition(JUPITER)?.lng
    val venusDeg = h.getPosition(VENUS)?.lng

    return planetDeg?.let {
      val jupResult = jupiterDeg?.takeIf {
        planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, SEXTILE, 1.0)
      }?.let { "comment" to arrayOf(planet, JUPITER, SEXTILE) }

      val venResult = venusDeg?.takeIf {
        planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, SEXTILE, 1.0)
      }?.let { "comment" to arrayOf(planet, VENUS, SEXTILE) }

      if (jupResult != null)
        return@let jupResult
      else
        return@let venResult
    }
  }
}
