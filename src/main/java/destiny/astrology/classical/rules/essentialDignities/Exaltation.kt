/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:29:49
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.EssentialUtils

/** A planet in its exaltation , or mutual reception with another planet by exaltation  */
class Exaltation(private val dayNightDifferentiatorImpl: DayNightDifferentiator) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign ->
      if (planet === essentialImpl.getPoint(sign, Dignity.EXALTATION)) {
        logger.debug("{} 位於其 {} 的星座 {}", planet, Dignity.EXALTATION, sign)
        return@let "commentBasic" to arrayOf(planet, sign)
      } else
        return@let exaltMutualReception(h, planet)
    }
  }

  /**
   * 廟廟互容
   * [Dignity.EXALTATION] 互容
   */
  private fun exaltMutualReception(h: Horoscope, planet: Planet): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign1: ZodiacSign ->
      essentialImpl.getPoint(sign1, Dignity.EXALTATION)?.let { signExaltation ->
        val utils = EssentialUtils(dayNightDifferentiatorImpl)
        utils.setEssentialImpl(essentialImpl)
        h.getZodiacSign(signExaltation)
          ?.takeIf { sign2 ->
            planet === essentialImpl.getPoint(sign2, Dignity.EXALTATION)
          }?.takeIf { sign2: ZodiacSign ->
          !utils.isBothInBadSituation(planet, sign1, signExaltation, sign2)
        }?.let { sign2: ZodiacSign ->
          logger.debug("{} 位於 {} , 與其 {} {} 飛至 {} , 形成 廟廟互容", planet, sign1, Dignity.EXALTATION, signExaltation, sign2)
          "commentReception" to arrayOf(planet, sign1, signExaltation, sign2)
        }
      }
    }
  }
}
