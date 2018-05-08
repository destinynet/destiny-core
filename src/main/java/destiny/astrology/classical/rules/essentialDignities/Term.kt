/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:26:46
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.IHoro
import destiny.astrology.Planet
import destiny.astrology.classical.ITerm

/** A planet in itw own term.  */
class Term(private val termImpl : ITerm) : Rule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    val lngDeg: Double? = h.getPosition(planet)?.lng
    return lngDeg?.let {
      val termPoint =  termImpl.getPoint(lngDeg)
      if (planet === termPoint)
        return@let "comment" to arrayOf(planet, lngDeg)
      else
        return@let null
    }
  }
}
