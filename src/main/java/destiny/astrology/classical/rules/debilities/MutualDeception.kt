/**
 * @author smallufo
 * Created on 2008/1/1 at 上午 9:36:03
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity

/**
 * 互容的變形，兩星都處與落陷，又互容→互相扯後腿<br></br>
 * 例如： 水星到雙魚 , 木星到處女<br></br>
 * 水星在雙魚為 Detriment/Fall , 雙魚的 Ruler 為木星 , 木星到處女為 Detriment
 */
class MutualDeception : EssentialRule(), Applicable {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return rulerMutualDeception(h, planet)
      ?: exaltationMutualDeception(h, planet)
      ?: detrimentExaltationMutualDeception(h, planet)
      ?: fallExaltationMutualDeception(h, planet)
  }

  /**
   * @return Ruler 的 互陷 或 互落
   * planet 在此 horoscope 中，座落到 sign1 星座
   * 而 sign1 星座的 ruler 星，飛到 sign2 星座
   * 而 sign2 星座的 ruler (planet2) 剛好等於 planet
   */
  private fun rulerMutualDeception(h: Horoscope, planet: Planet): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign1 ->
      essentialImpl.getPoint(sign1, Dignity.RULER)?.let { signRuler ->
        h.getZodiacSign(signRuler)?.let { sign2 ->
          essentialImpl.getPoint(sign2, Dignity.RULER)?.takeIf { planet2 ->
            // 確定 ruler 互容
            planet === planet2
          }?.takeIf {
            // 確認互陷
            essentialImpl.isBothInBadSituation(planet, sign1, signRuler, sign2)
          }?.let {
            logger.debug("[comment1] {} 位於 {} , 與其 {} {} 飛至 {} , 形成 {} 互陷", planet, sign1, Dignity.RULER, signRuler, sign2, Dignity.RULER)
            "comment1" to arrayOf(planet, sign1, signRuler, sign2)
          }
        }
      }
    }
  } // ruler 互陷 或 互落

  /**
   * @return Exaltation 的 互陷 或 互落
   */
  private fun exaltationMutualDeception(h: Horoscope, planet: Planet): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign1 ->
      essentialImpl.getPoint(sign1, Dignity.EXALTATION)?.let { signExalt ->
        h.getZodiacSign(signExalt)?.let { sign2 ->
          essentialImpl.getPoint(sign2, Dignity.EXALTATION)?.takeIf { planet2 ->
            // 確認 Exaltation 互容
            planet === planet2
          }?.takeIf {
            // 確認互陷
            essentialImpl.isBothInBadSituation(planet, sign1, signExalt, sign2)
          }?.let {
            logger.info("[comment2] {} 位於 {} , 與其 {} {} 飛至 {} , 形成 {} 互陷", planet, sign1, Dignity.EXALTATION, signExalt, sign2, Dignity.EXALTATION)
            "comment2" to arrayOf(planet, sign1, signExalt, sign2)
          }
        }
      }
    }
  }

  /**
   * 旺廟互陷
   * 「Ruler 到 Detriment , Exaltation 到 Fall 又互容」的互陷
   */
  private fun detrimentExaltationMutualDeception(h: Horoscope, planet: Planet): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign1 ->
      essentialImpl.getPoint(sign1, Dignity.RULER)?.let { signRuler ->
        h.getZodiacSign(signRuler)?.let { sign2 ->
          essentialImpl.getPoint(sign2, Dignity.EXALTATION)?.takeIf { planet2 ->
            // 確認互容
            planet === planet2
          }?.takeIf {
            // 確認互陷
            essentialImpl.isBothInBadSituation(planet, sign1, signRuler, sign2)
          }?.let {
            logger.debug("[comment3] {} 位於 {} , 與其 {} {} 飛至 {} , 形成 廟旺互陷", planet, sign1, Dignity.RULER, signRuler, sign2)
            "comment3" to arrayOf(planet, sign1, signRuler, sign2)
          }
        }
      }
    }
  }

  /**
   * 旺廟互陷
   * 「Ruler 到 Fall , Exaltation 到 Detriment 又互容」的互陷
   */
  private fun fallExaltationMutualDeception(h: Horoscope, planet: Planet): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign1 ->
      essentialImpl.getPoint(sign1, Dignity.EXALTATION)?.let { signExalt ->
        h.getZodiacSign(signExalt)?.let { sign2 ->
          essentialImpl.getPoint(sign2, Dignity.RULER)?.takeIf { planet2 ->
            // 確認互容
            planet === planet2
          }?.takeIf {
            // 確認互陷
            essentialImpl.isBothInBadSituation(planet, sign1, signExalt, sign2)
          }?.let {
            logger.debug("[comment4] {} 位於 {} , 與其 {} {} 飛至 {} , 形成 廟旺互陷", planet, sign1, Dignity.EXALTATION, signExalt, sign2)
            "comment4" to arrayOf(planet, sign1, signExalt, sign2)
          }
        }
      }
    }
  }


}
