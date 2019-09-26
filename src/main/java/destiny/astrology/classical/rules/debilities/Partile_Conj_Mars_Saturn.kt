/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:27:33
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Aspect
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.MARS
import destiny.astrology.Planet.SATURN

/** Partile conjunction with Mars or Saturn.  */
class Partile_Conj_Mars_Saturn : DebilityRule() {

  private val aspect = Aspect.CONJUNCTION

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val marsDeg = h.getPosition(MARS)?.lng
    val saturnDeg = h.getPosition(SATURN)?.lng

    return planetDeg?.let {
      val marResult = marsDeg?.takeIf {
        planet !== MARS && IHoroscopeModel.getAngle(planetDeg, marsDeg) <= 1
      }?.let { "comment" to arrayOf(planet, MARS, aspect) }

      val satResult = saturnDeg?.takeIf {
        planet != SATURN && IHoroscopeModel.getAngle(planetDeg, saturnDeg) <= 1
      }?.let { "comment" to arrayOf(planet, SATURN, aspect) }

      marResult ?: satResult
    }
  }
}
