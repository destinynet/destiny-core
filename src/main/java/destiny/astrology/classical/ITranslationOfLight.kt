/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical

import destiny.astrology.IAspectApplySeparate
import destiny.astrology.IHoro
import destiny.astrology.Planet

interface ITranslationOfLight {

  fun getResult(planet: Planet, h: IHoro): Triple<Planet, Planet, IAspectApplySeparate.AspectType?>?
}
