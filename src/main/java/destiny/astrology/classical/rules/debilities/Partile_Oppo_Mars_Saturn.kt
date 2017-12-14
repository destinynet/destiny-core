/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:38:12
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Aspect.OPPOSITION
import destiny.astrology.AspectEffectiveModern
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.MARS
import destiny.astrology.Planet.SATURN
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*
import java.util.Optional.empty

/** Partile opposite Mars or Saturn.  */
class Partile_Oppo_Mars_Saturn : Rule() {

  private val aspect = OPPOSITION

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getPositionOpt(planet).map<Double>({ it.lng }).flatMap { planetDegree ->
      h.getPositionOpt(MARS).map<Double>({ it.lng }).flatMap { marsDeg ->
        h.getPositionOpt(SATURN).map<Double>({ it.lng }).flatMap { saturnDeg ->
          if (planet !== MARS && AspectEffectiveModern.isEffective(planetDegree!!, marsDeg!!, aspect, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, MARS, aspect)
            Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, MARS, aspect)))
          } else if (planet !== SATURN && AspectEffectiveModern.isEffective(planetDegree!!, saturnDeg!!, aspect, 1.0)) {
            logger.debug("{} 與 {} 形成 {}", planet, SATURN, aspect)
            Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, SATURN, aspect)))
          }
          empty<Tuple2<String, Array<Any>>>()
        }
      }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val marsDeg = h.getPosition(MARS)?.lng
    val saturnDeg = h.getPosition(SATURN)?.lng

    return planetDeg?.let {
      val marResult = marsDeg?.takeIf {
        planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, aspect, 1.0)
      }?.let { "comment" to arrayOf(planet, MARS, aspect) }

      val satResult = saturnDeg?.takeIf {
        planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, aspect, 1.0)
      }?.let { "comment" to arrayOf(planet, SATURN, aspect) }

      if (marResult != null)
        return@let marResult
      else
        return@let satResult
    }
  }
}
