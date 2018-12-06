/**
 * Created by smallufo at 2008/11/11 下午 8:29:36
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.astrology.Point
import destiny.astrology.classical.IRefranation

/**
 * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
 */
class Refrain_from_Mars_Saturn(private val refranationImpl: IRefranation) : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    // 太陽 / 月亮不會逆行
    return planet.takeIf { it !== MOON && it !== SUN }
      ?.let {
        var otherPoint: Point

        if (planet !== MARS) {
          otherPoint = MARS

          refranationImpl.getImportantResult(h , planet , otherPoint)?.let { pair ->
            val aspect = pair.second
            logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, otherPoint, aspect)
            return "comment" to arrayOf(planet, otherPoint, aspect)
          }
        }

        if (planet !== SATURN) {
          otherPoint = SATURN

          refranationImpl.getImportantResult(h , planet , otherPoint)?.let { pair ->
            val aspect = pair.second
            logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, otherPoint, aspect)
            return "comment" to arrayOf(planet, otherPoint, aspect)
          }
        }
        return@let null
      }
  }
}
