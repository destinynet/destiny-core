/**
 * @author smallufo
 * Created on 2007/12/29 at 下午 11:45:23
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.*
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile conjunction with Dragon's Head (Moon's North Node).  */
class Partile_Conj_North_Node : Rule() {

  /** 內定採用 NodeType.MEAN  */
  var nodeType = NodeType.MEAN

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {

    return h.getPositionOpt(planet).map<Double> { it.lng }.flatMap { planetDegree ->
      val north = LunarNode.of(LunarNode.NorthSouth.NORTH, nodeType)
      h.getPositionOpt(north).map<Double> { it.lng }.flatMap { northDeg ->
        if (Horoscope.getAngle(planetDegree!!, northDeg!!) <= 1) {
          logger.debug("{} 與 {} 形成 {}", planet, north, Aspect.CONJUNCTION)
          Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf<Any>(planet, north, Aspect.CONJUNCTION)))
        }
        Optional.empty<Tuple2<String, Array<Any>>>()
      }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {

    val planetDeg: Double? = h.getPosition(planet)?.lng
    val north: LunarNode = LunarNode.of(LunarNode.NorthSouth.NORTH, nodeType)
    val northDeg: Double? = h.getPosition(north)?.lng

    return if (planetDeg != null && northDeg != null && Horoscope.getAngle(planetDeg , northDeg) <= 1) {
      logger.debug("{} 與 {} 形成 {}", planet, north, Aspect.CONJUNCTION)
      "comment" to arrayOf(planet , north , Aspect.CONJUNCTION)
    } else {
      null
    }

  }
}
