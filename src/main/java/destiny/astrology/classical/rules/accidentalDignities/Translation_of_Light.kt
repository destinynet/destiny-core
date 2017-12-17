/**
 * @author smallufo
 * Created on 2008/1/5 at 上午 7:14:47
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.ITranslationOfLight

class Translation_of_Light(private val translationOfLightImpl: ITranslationOfLight) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
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
