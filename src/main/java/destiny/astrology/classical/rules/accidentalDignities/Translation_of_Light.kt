/**
 * @author smallufo
 * Created on 2008/1/5 at 上午 7:14:47
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.TranslationOfLightIF
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

class Translation_of_Light(private val translationOfLightImpl: TranslationOfLightIF) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h)?.let { p -> Tuple.tuple(p.first , p.second) }.let { Optional.ofNullable(it) }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return translationOfLightImpl.getResult(planet, h)
      ?.takeIf { it.v1 == true }
      ?.let { t ->
        val deg = h.getAngle(t.v2 , t.v3)
        if (t.v4 != null)
          return@let "commentAspect" to arrayOf(planet , t.v2 , t.v3 , deg , t.v4!!)
        else
          return@let "commentUnaspect" to arrayOf(planet , t.v2 , t.v3 , deg)
      }
  }
}
