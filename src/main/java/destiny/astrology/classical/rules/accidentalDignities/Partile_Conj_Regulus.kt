/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:00:27
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.*
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000.  */
class Partile_Conj_Regulus : Rule() {

  private val aspect = Aspect.CONJUNCTION
  
  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg: Double? = h.getPosition(planet)?.lng
    val regulusDeg: Double? = h.getPosition(FixedStar.REGULUS)?.lng
    return if (planetDeg != null && regulusDeg != null && AspectEffectiveModern.isEffective(planetDeg, regulusDeg, aspect, 1.0)) {
      logger.debug("{} 與 {} 形成 {}", planet, FixedStar.REGULUS, aspect)
      "comment" to arrayOf(planet, FixedStar.REGULUS, aspect)
    } else
      null
  }
}
