/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:50:48
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Aspect.TRINE
import destiny.astrology.AspectEffectiveModern
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.JUPITER
import destiny.astrology.Planet.VENUS
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile trine Jupiter or Venus.  */
class Partile_Trine_Jupiter_Venus : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getPositionOpt(planet).map<Double>({ it.lng }).flatMap { planetDegree ->
      h.getPositionOpt(JUPITER).map<Double>({ it.lng }).flatMap { jupiterDeg ->
        h.getPositionOpt(VENUS).map<Double>({ it.lng }).flatMap { venusDeg ->
          if (planet !== JUPITER && AspectEffectiveModern.isEffective(planetDegree!!, jupiterDeg!!, TRINE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, JUPITER, TRINE)
            Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, JUPITER, TRINE)))
          } else if (planet !== VENUS && AspectEffectiveModern.isEffective(planetDegree!!, venusDeg!!, TRINE, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, VENUS, TRINE)
            Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, VENUS, TRINE)))
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
        planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg , jupiterDeg , TRINE , 1.0)
      }?.let { "comment" to arrayOf(planet , JUPITER , TRINE) }

      val venResult = venusDeg?.takeIf {
        planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg , venusDeg , TRINE , 1.0)
      }?.let { "comment" to arrayOf(planet , VENUS , TRINE) }

      if (jupResult != null)
        return@let jupResult
      else
        return@let venResult
    }

  }
}
