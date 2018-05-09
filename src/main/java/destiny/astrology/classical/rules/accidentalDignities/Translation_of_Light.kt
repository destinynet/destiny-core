/**
 * @author smallufo
 * Created on 2008/1/5 at 上午 7:14:47
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.ITranslationOfLight

class Translation_of_Light(private val translationOfLightImpl: ITranslationOfLight) : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return translationOfLightImpl.getResult(planet, h)
      ?.let { t ->
        val deg = h.getAngle(t.first , t.second)
        if (t.third != null) {
          return@let "commentAspect" to arrayOf(planet , t.first , t.second , deg , t.third!!)
        } else {
          return@let "commentUnaspect" to arrayOf(planet , t.first , t.second , deg)
        }
      }

  }
}
