/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:29:49
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.IEssential
import destiny.astrology.classical.IExaltation

/** A planet in its exaltation , or mutual reception with another planet by exaltation  */
class Exaltation(private val essentialImpl: IEssential,
                 private val exaltImpl: IExaltation) : EssentialRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign ->
      if (planet === with(exaltImpl) { sign.getExaltPoint() }) {
        logger.debug("{} 位於其 {} 的星座 {}", planet, Dignity.EXALTATION, sign)
        "commentBasic" to arrayOf(planet, sign)
      } else
        exaltMutualReception(h, planet)
    }
  }

  /**
   * 廟廟互容
   * [Dignity.EXALTATION] 互容
   */
  private fun exaltMutualReception(h: IHoroscopeModel, planet: Planet): Pair<String, Array<Any>>? {

    return with(essentialImpl) {
      planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.EXALTATION)).firstOrNull()?.let { mutualData ->
        val sign1 = h.getZodiacSign(planet)!!

        val sign2 = h.getZodiacSign(mutualData.getAnotherPoint(planet))!!
        "commentReception" to arrayOf(planet, sign1, mutualData.getAnotherPoint(planet), sign2)
      }
    }

  }
}
