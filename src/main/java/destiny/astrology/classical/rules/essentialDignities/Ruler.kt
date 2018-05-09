/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:18:14
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.IEssential

/** A planet in its own sign , or mutual reception with another planet by sign  */
class Ruler(private val essentialImpl: IEssential) : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
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
  private fun rulerMutualReception(h: IHoroscopeModel, planet: Planet): Pair<String, Array<Any>>? {
    return essentialImpl.getMutualData(planet , h.pointDegreeMap , null , setOf(Dignity.RULER)).firstOrNull()?.let { mutualData ->
      val sign1 = h.getZodiacSign(planet)!!
      val sign2 = h.getZodiacSign(mutualData.p2)!!
      "commentReception" to arrayOf(planet , sign1 , mutualData.p2 , sign2)
    }
  }
}
