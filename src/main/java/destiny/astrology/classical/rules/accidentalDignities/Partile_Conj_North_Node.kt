/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:45:23
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.*

/** Partile aspect with Dragon's Head (Moon's North Node).  */
class Partile_Conj_North_Node : Rule() {
  
  private val aspect = Aspect.CONJUNCTION

  /** 內定採用 NodeType.MEAN  */
  var nodeType = NodeType.MEAN

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {

    val planetDeg: Double? = h.getPosition(planet)?.lng
    val north: LunarNode = LunarNode.of(NorthSouth.NORTH, nodeType)
    val northDeg: Double? = h.getPosition(north)?.lng

    return if (planetDeg != null && northDeg != null && IHoroscopeModel.getAngle(planetDeg, northDeg) <= 1) {
      logger.debug("{} 與 {} 形成 {}", planet, north, aspect)
      "comment" to arrayOf(planet , north , aspect)
    } else {
      null
    }

  }
}
