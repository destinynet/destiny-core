/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 3:50:48
 */
package destiny.astrology.classical

import destiny.astrology.*
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.*
import java.io.Serializable

/**
 * 取得星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail
 * 內定實作為 托勒密表格
 */
class EssentialRedfDefaultImpl : IEssentialRedf, Serializable {

  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 empty() ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 empty()
   */
  override fun getPoint(sign: ZodiacSign, dignity: Dignity): Point? {
    return when (dignity) {
    /** 廟 , +4  */
      EXALTATION -> findPoint(sign, starExaltationMap) // nullable
    /** 落 , -4  */
      FALL -> findPoint(sign, starFallMap) // nullable
      else -> essDigMap[(sign to dignity)] // not null
    }
  }

  private fun findPoint(sign: ZodiacSign, map: Map<Point, Double>): Point? {
    return map.entries
      .filter { (_, value) -> sign === getZodiacSign(value) }
      .map { entry -> entry.key }
      .firstOrNull()
  }


  /**
   * 取得在此星座得到「Exaltation , 廟 +4」的星體及度數
   */
  fun getExaltationStarDegree(sign: ZodiacSign): PointDegree? {
    return findPoint(sign , starExaltationMap)?.let { point -> PointDegree(point , starExaltationMap[point]!!) }
  }

  /**
   * 取得在此星座得到「Fall , 落 -4」的星體及度數
   */
  fun getFallStarDegree(sign: ZodiacSign): PointDegree? {
    return findPoint(sign , starFallMap)?.let { point -> PointDegree(point , starFallMap[point]!!) }
  }

  companion object {
    /** 存放星體在黃道帶上幾度得到 Exaltation (廟 , +4) 的度數  */
    private val starExaltationMap = mapOf<Point, Double>(
       Planet.SUN to 19.0  // 太陽在戌宮 19度 exalted.
      ,Planet.MOON to 33.0  // 月亮在酉宮 03度 exalted.
      ,Planet.MERCURY to 165.0  // 水星在巳工 15度 exalted.
      ,Planet.VENUS to 357.0  // 金星在亥宮 27度 exalted.
      ,Planet.MARS to 298.0  // 火星在丑宮 28度 exalted.
      ,Planet.JUPITER to 105.0  // 木星在未宮 15度 exalted.
      ,Planet.SATURN to 201.0  // 土星在辰宮 21度 exalted.
      ,LunarNode.NORTH_TRUE to 63.0  //北交點在 申宮 03度 exalted.
      ,LunarNode.NORTH_MEAN to 63.0  //北交點在 申宮 03度 exalted.
      ,LunarNode.SOUTH_TRUE to 243.0  //南交點在 寅宮 03度 exalted.
      ,LunarNode.SOUTH_MEAN to 243.0  //南交點在 寅宮 03度 exalted.
    )


    /**
     * 放星體在黃道帶上幾度得到 Fall (落 , -4) 的度數
     * 前面 starExaltationMap 中，每個星體的度數 +180度即為「落」
     */

    private val starFallMap = starExaltationMap.keys.map {
        it to Utils.getNormalizeDegree(starExaltationMap[it]!! + 180)
      }.toMap()

    private val rulerMap = mapOf<Pair<ZodiacSign, Dignity>, Planet>(
      (ARIES to RULER) to Planet.MARS,
      (TAURUS to RULER) to Planet.VENUS,
      (GEMINI to RULER) to Planet.MERCURY,
      (CANCER to RULER) to Planet.MOON,
      (LEO to RULER) to Planet.SUN,
      (VIRGO to RULER) to Planet.MERCURY,
      (LIBRA to RULER) to Planet.VENUS,
      (SCORPIO to RULER) to Planet.MARS,
      (SAGITTARIUS to RULER) to Planet.JUPITER,
      (CAPRICORN to RULER) to Planet.SATURN,
      (AQUARIUS to RULER) to Planet.SATURN,
      (PISCES to RULER) to Planet.JUPITER
    )

    private val essDigMap: Map<Pair<ZodiacSign, Dignity>, Planet> = rulerMap.toMutableMap().let {
      ZodiacSign.values().map { sign ->
        val key = (sign to DETRIMENT)
        val value = rulerMap[sign.oppositeSign to RULER]!!
        it.put(key, value)
      }
      it
    }.toMap()

  }

}
