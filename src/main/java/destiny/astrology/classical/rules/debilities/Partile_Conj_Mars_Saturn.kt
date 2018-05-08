/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:27:33
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Aspect
import destiny.astrology.IHoro
import destiny.astrology.Planet
import destiny.astrology.Planet.MARS
import destiny.astrology.Planet.SATURN

/** Partile conjunction with Mars or Saturn.  */
class Partile_Conj_Mars_Saturn : Rule() {

  private val aspect = Aspect.CONJUNCTION

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val marsDeg = h.getPosition(MARS)?.lng
    val saturnDeg = h.getPosition(SATURN)?.lng

    return planetDeg?.let {
      val marResult = marsDeg?.takeIf {
        planet !== MARS && IHoro.getAngle(planetDeg, marsDeg) <= 1
      }?.let { "comment" to arrayOf(planet, MARS, aspect) }

      val satResult = saturnDeg?.takeIf {
        planet != SATURN && IHoro.getAngle(planetDeg, saturnDeg) <= 1
      }?.let { "comment" to arrayOf(planet, SATURN, aspect) }

      if (marResult != null)
        return@let marResult
      else
        return@let satResult
    }
  }
}
