/**
 * Created by smallufo at 2008/11/11 下午 10:09:47
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.astrology.Point
import destiny.astrology.classical.RefranationIF
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*

class Refrain_from_Venus_Jupiter(private val refranationImpl: RefranationIF) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    //太陽 / 月亮不會逆行
    if (planet === Planet.MOON || planet === Planet.SUN)
      return Optional.empty()

    var otherPoint: Point

    if (planet !== Planet.VENUS) {
      otherPoint = Planet.VENUS

      refranationImpl.getImportantResult(h, planet, otherPoint)?.let {
        val aspect = it.second
        logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, otherPoint, aspect)
        return Optional.of(Tuple.tuple("comment", arrayOf(planet, otherPoint, aspect)))
      }
    }

    if (planet !== Planet.JUPITER) {
      otherPoint = Planet.JUPITER


      refranationImpl.getImportantResult(h, planet, otherPoint)?.let {
        val aspect = it.second
        logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, otherPoint, aspect)
        return Optional.of(Tuple.tuple("comment", arrayOf(planet, otherPoint, aspect)))
      }


    }
    return Optional.empty()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    // 太陽 / 月亮不會逆行
    return planet.takeIf { it !== SUN && it !== MOON }
      ?.let {

        var otherPoint: Point

        if (planet !== VENUS) {
          otherPoint = VENUS

          refranationImpl.getImportantResult(h, planet, otherPoint)?.let {
            val aspect = it.second
            logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, otherPoint, aspect)
            return@let "comment" to arrayOf(planet, otherPoint, aspect)
          }
        }

        if (planet !== JUPITER) {
          otherPoint = JUPITER

          refranationImpl.getImportantResult(h, planet, otherPoint)?.let {
            val aspect = it.second
            logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, otherPoint, aspect)
            return@let "comment" to arrayOf(planet , otherPoint , aspect)
          }
        }
        return@let null
      }

  }
}
