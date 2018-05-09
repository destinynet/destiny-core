/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:44:04
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*

/** Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000.  */
class Conj_Algol : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDeg: Double? = h.getPosition(planet)?.lng
    val algolDeg: Double? = h.getPosition(FixedStar.ALGOL)?.lng
    return if (planetDeg != null && algolDeg != null && AspectEffectiveModern.isEffective(planetDeg , algolDeg , Aspect.CONJUNCTION , 5.0)) {
      logger.debug("{} 與 {} 形成 {}", planet, FixedStar.ALGOL, Aspect.CONJUNCTION)
      "comment" to arrayOf(planet, FixedStar.ALGOL, Aspect.CONJUNCTION)
    } else
      null
  }
}
