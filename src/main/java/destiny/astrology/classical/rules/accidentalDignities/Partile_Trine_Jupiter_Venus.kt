/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:50:48
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Aspect.TRINE
import destiny.astrology.AspectEffectiveModern
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.JUPITER
import destiny.astrology.Planet.VENUS

/** Partile trine Jupiter or Venus.  */
class Partile_Trine_Jupiter_Venus : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
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
