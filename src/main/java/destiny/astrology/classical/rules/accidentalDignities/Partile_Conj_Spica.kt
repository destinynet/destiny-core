/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:02:30
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.*

/** Partile conjunct Spica at 23deg50' Libra in January 2000.  */
class Partile_Conj_Spica : AccidentalRule() {

  private val aspect = Aspect.CONJUNCTION

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDeg: Double? = h.getPosition(planet)?.lng
    val spicaDeg : Double? = h.getPosition(FixedStar.SPICA)?.lng

    return if (planetDeg != null && spicaDeg != null && AspectEffectiveModern.isEffective(planetDeg , spicaDeg , aspect , 1.0)) {
      logger.debug("{} 與 {} 形成 {}", planet, FixedStar.SPICA, aspect)
      "comment" to arrayOf(planet , FixedStar.SPICA , aspect)
    } else
      null
  }
}
