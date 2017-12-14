/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:44:04
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000.  */
class Conj_Algol : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {

    return h.getPositionOpt(planet).map<Double> { it.lng }.flatMap { planetDegree ->
      h.getPositionOpt(FixedStar.ALGOL).map<Double> { it.lng }.flatMap { algolDeg ->
        if (AspectEffectiveModern.isEffective(planetDegree!!, algolDeg!!, Aspect.CONJUNCTION, 5.0)) {
          logger.debug("{} 與 {} 形成 {}", planet, FixedStar.ALGOL, Aspect.CONJUNCTION)
          Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf(planet, FixedStar.ALGOL, Aspect.CONJUNCTION)))
        }
        Optional.empty<Tuple2<String, Array<Any>>>()
      }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg: Double? = h.getPosition(planet)?.lng
    val algolDeg: Double? = h.getPosition(FixedStar.ALGOL)?.lng
    return if (planetDeg != null && algolDeg != null && AspectEffectiveModern.isEffective(planetDeg , algolDeg , Aspect.CONJUNCTION , 5.0)) {
      logger.debug("{} 與 {} 形成 {}", planet, FixedStar.ALGOL, Aspect.CONJUNCTION)
      "comment" to arrayOf(planet, FixedStar.ALGOL, Aspect.CONJUNCTION)
    } else
      null
  }
}
