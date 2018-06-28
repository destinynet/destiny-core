/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 3:56:55
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.IEssential

/**
 * 廟旺互容
 * 舉例：水星到摩羯，火星到雙子
 * 摩羯為火星 Exaltation 之星座，雙子為水星 Ruler 之星座
 */
class MixedReception(private val essentialImpl: IEssential) : EssentialRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return mixedReception(h , planet)
  }

  private fun mixedReception(h: IHoroscopeModel, planet: Planet): Pair<String, Array<Any>>? {
    return essentialImpl.getMutualData(planet , h.pointDegreeMap , null, setOf(Dignity.RULER , Dignity.EXALTATION)).firstOrNull()?.let { mutualData ->
      val sign1 = h.getZodiacSign(planet)!!
      val sign2 = h.getZodiacSign(mutualData.p2)!!
      logger.debug("mutualData = {}" , mutualData)
      logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 RULER/EXALT 互容" ,
              planet , sign1 , mutualData.dig2 , mutualData.p2 , sign2 , sign2 , mutualData.dig1 , mutualData.p1 , sign1)
      "comment" to arrayOf(planet , sign1 , mutualData.p2 , sign2)
    }
  }


}
