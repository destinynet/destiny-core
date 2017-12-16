/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:29:56
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** A planet in its own Chaldean decanate or face.  */
class Face : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getPosition(planet)?.lng?.takeIf { lngDeg ->
      val facePoint = essentialImpl.getFacePoint(lngDeg)
      return@takeIf (planet === facePoint)
    }?.let { lngDeg ->
      logger.info("{} 位於其 Chaldean decanate or face : {}", planet, lngDeg)
      "comment" to arrayOf(planet , lngDeg)
    }
  }
}
