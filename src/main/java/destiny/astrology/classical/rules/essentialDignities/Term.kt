/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:26:46
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** A planet in itw own term.  */
class Term : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val lngDeg: Double? = h.getPosition(planet)?.lng
    return lngDeg?.let {
      val termPoint = essentialImpl.getTermsPoint(lngDeg)
      if (planet === termPoint)
        return@let "comment" to arrayOf(planet, lngDeg)
      else
        return@let null
    }
  }
}
