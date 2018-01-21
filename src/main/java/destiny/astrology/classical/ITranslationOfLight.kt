/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical

import destiny.astrology.Horoscope
import destiny.astrology.IAspectApplySeparate
import destiny.astrology.Planet

interface ITranslationOfLight {

  fun getResult(planet: Planet, h: Horoscope): Triple<Planet, Planet, IAspectApplySeparate.AspectType?>?
}
