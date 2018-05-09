/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:42:03
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Aspect
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.JUPITER
import destiny.astrology.Planet.VENUS

/** Partile conjunction with Jupiter or Venus.
 * 和金星或木星合相，交角 1 度內  */
class Partile_Conj_Jupiter_Venus : Rule() {

  private val aspect = Aspect.CONJUNCTION

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val jupiterDeg = h.getPosition(JUPITER)?.lng
    val venusDeg = h.getPosition(VENUS)?.lng

    return planetDeg?.let {
      val jupResult = jupiterDeg?.takeIf {
        planet !== JUPITER && IHoroscopeModel.getAngle(planetDeg, jupiterDeg) <= 1
      }?.let { "comment" to arrayOf(planet, JUPITER, aspect) }

      val venResult = venusDeg?.takeIf {
        planet !== VENUS && IHoroscopeModel.getAngle(planetDeg, venusDeg) <= 1
      }?.let { "comment" to arrayOf(planet, VENUS, aspect) }

      if (jupResult != null)
        return@let jupResult
      else
        return@let venResult
    }


  }
}
