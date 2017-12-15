/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:27:33
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Aspect
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.MARS
import destiny.astrology.Planet.SATURN
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile conjunction with Mars or Saturn.  */
class Partile_Conj_Mars_Saturn : Rule() {

  private val aspect = Aspect.CONJUNCTION

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val marsDeg = h.getPosition(MARS)?.lng
    val saturnDeg = h.getPosition(SATURN)?.lng

    return planetDeg?.let {
      val marResult = marsDeg?.takeIf {
        planet !== MARS && Horoscope.getAngle(planetDeg, marsDeg) <= 1
      }?.let { "comment" to arrayOf(planet, MARS, aspect) }

      val satResult = saturnDeg?.takeIf {
        planet != SATURN && Horoscope.getAngle(planetDeg , saturnDeg) <= 1
      }?.let { "comment" to arrayOf(planet, SATURN, aspect) }

      if (marResult != null)
        return@let marResult
      else
        return@let satResult
    }
  }
}
