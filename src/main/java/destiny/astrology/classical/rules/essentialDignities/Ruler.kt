/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:18:14
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.EssentialTools
import destiny.astrology.classical.IDetriment
import destiny.astrology.classical.IFall

/** A planet in its own sign , or mutual reception with another planet by sign  */
class Ruler(private val detrimentImpl : IDetriment ,
            private val fallImpl : IFall) : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return h.getZodiacSign(planet)?.let { sign ->
      return if (planet === rulerImpl.getPoint(sign)) {
        logger.debug("{} 位於 {} , 為其 {}", planet, sign, Dignity.RULER)
        "commentBasic" to arrayOf(planet, sign)
      } else {
        logger.debug("檢查旺旺互容 of {}", planet)
        rulerMutualReception(h , planet)
      }
    }
  }

  /**
   * 旺旺互容 (mutual reception)
   * RULER / RULER 互容
   * planet 在此 horoscope 中，座落到 sign1 星座
   * 而 sign1 星座的 RULER 星，飛到 sign2 星座
   * 而 sign2 星座的 RULER 星 (planet2) 剛好等於 planet
   *
   * 例如： 火星在射手 , 木星在牡羊 , 兩個星座的 Ruler 互訪<br></br>
   * 「而且都沒有落陷」 (否則變成互陷)
   */
  private fun rulerMutualReception(h: Horoscope, planet: Planet): Pair<String, Array<Any>>? {

    // 取得此 planet 在什麼星座
    val sign1: ZodiacSign? = h.getZodiacSign(planet)
    // 此星座的 ruler 是什麼星
    val signRuler: Point? = sign1?.let {
      rulerImpl.getPoint(it)
      //essentialImpl.getPoint(it , Dignity.RULER)
    }
    // 該星飛到什麼星座
    val sign2: ZodiacSign? = signRuler?.let { h.getZodiacSign(it) }

    if (sign1 != null && signRuler != null && sign2 != null
      && planet === rulerImpl.getPoint(sign2)) {
      if (
        // 已經確定 Ruler 互容，要排除互陷
        !EssentialTools.isBothInBadSituation(planet , sign1 , signRuler , sign2 , detrimentImpl , fallImpl)
      ) {
        // FIXME : 其實這並非「旺旺互容」，因為並沒有檢查 planet 在 sign1 是否「旺」 , 也沒檢查 signRuler 在 sign2 是否「旺」
        logger.debug("{} 位於 {} , 與其 Ruler {} 飛至 {} , 形成 旺旺互容", planet, sign1, signRuler, sign2)
        return "commentReception" to arrayOf(planet, sign1, signRuler, sign2)
      }
    }
    return null
  }
}
