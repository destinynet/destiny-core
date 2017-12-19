/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical

import destiny.astrology.Horoscope
import destiny.astrology.IAspectApplySeparate
import destiny.astrology.Planet
import org.jooq.lambda.tuple.Tuple4

interface ITranslationOfLight {

  fun getResult(planet: Planet, h: Horoscope): Tuple4<Boolean, Planet, Planet, IAspectApplySeparate.AspectType?>?
}
