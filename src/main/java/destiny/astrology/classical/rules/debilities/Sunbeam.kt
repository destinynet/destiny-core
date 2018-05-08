/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:25:34
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoro
import destiny.astrology.Planet
import destiny.astrology.Planet.SUN

/** Under the Sunbeams (between 8.5 and 17 from Sol).  */
class Sunbeam : Rule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    return planet.takeIf { it !== SUN }
      ?.takeIf { h.getAngle(it , SUN) > 8.5 && h.getAngle(it , SUN) <= 17.0 }
      ?.let { "comment" to arrayOf<Any>(planet)}
  }
}
