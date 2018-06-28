/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:30:05
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*

/** Partile conjunction with Dragon's Tail (Moon's South Node).  */
class Partile_Conj_South_Node : DebilityRule() {

  /** 內定採用 NodeType.MEAN  */
  var nodeType = NodeType.MEAN

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val south = LunarNode.of(NorthSouth.SOUTH, nodeType)
    val southDeg = h.getPosition(south)?.lng

    return if (planetDeg != null && southDeg != null && IHoroscopeModel.getAngle(planetDeg, southDeg) <= 1)
      "comment" to arrayOf(planet , south , Aspect.CONJUNCTION)
    else
      null
  }
}
