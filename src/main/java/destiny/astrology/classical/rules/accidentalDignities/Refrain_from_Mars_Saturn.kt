/**
 * Created by smallufo at 2008/11/11 下午 8:29:36
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Point
import destiny.astrology.classical.RefranationIF
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/**
 * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
 */
class Refrain_from_Mars_Saturn(private val refranationImpl: RefranationIF) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    // 太陽 / 月亮不會逆行
    if (planet === Planet.MOON || planet === Planet.SUN)
      return Optional.empty()

    var otherPoint: Point

    if (planet !== Planet.MARS) {
      otherPoint = Planet.MARS

      refranationImpl.getImportantResult(h , planet , otherPoint)?.let {
        val aspect = it.second
        logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, otherPoint, aspect)
        return Optional.of(Tuple.tuple("comment", arrayOf(planet, otherPoint, aspect)))
      }
    }

    if (planet !== Planet.SATURN) {
      otherPoint = Planet.SATURN

      refranationImpl.getImportantResult(h , planet , otherPoint)?.let {
        val aspect = it.second
        logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, otherPoint, aspect)
        return Optional.of(Tuple.tuple("comment", arrayOf(planet, otherPoint, aspect)))
      }
    }
    return Optional.empty()
  }

}
