/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:30:05
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Partile conjunction with Dragon's Tail (Moon's South Node).  */
class Partile_Conj_South_Node : Rule() {

  /** 內定採用 NodeType.MEAN  */
  var nodeType = NodeType.MEAN

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return h.getPositionOpt(planet).map<Double>{ it.lng }.flatMap { planetDegree ->
      val south = LunarNode.of(LunarNode.NorthSouth.SOUTH, nodeType)
      h.getPositionOpt(south).map<Double>{ it.lng }.flatMap { southDeg ->
        if (Horoscope.getAngle(planetDegree!!, southDeg!!) <= 1) {
          logger.debug("{} 與 {} 形成 {}", planet, south, Aspect.CONJUNCTION)
          Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf<Any>(planet, south, Aspect.CONJUNCTION)))
        }
        Optional.empty<Tuple2<String, Array<Any>>>()
      }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    val planetDeg = h.getPosition(planet)?.lng
    val south = LunarNode.of(LunarNode.NorthSouth.SOUTH, nodeType)
    val southDeg = h.getPosition(south)?.lng

    return if (planetDeg != null && southDeg != null && Horoscope.getAngle(planetDeg , southDeg) <= 1)
      "comment" to arrayOf(planet , south , Aspect.CONJUNCTION)
    else
      null
  }
}
