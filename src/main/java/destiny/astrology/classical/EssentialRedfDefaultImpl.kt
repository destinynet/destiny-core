/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 3:50:48
 */
package destiny.astrology.classical

import com.google.common.collect.ImmutableMap
import destiny.astrology.*
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.*
import java.io.Serializable
import java.util.*

/**
 * 取得星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail <br></br>
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
    private val starExaltationMap = ImmutableMap.Builder<Point, Double>()
      .put(Planet.SUN, 19.0) // 太陽在戌宮 19度 exalted.
      .put(Planet.MOON, 33.0) // 月亮在酉宮 03度 exalted.
      .put(Planet.MERCURY, 165.0) // 水星在巳工 15度 exalted.
      .put(Planet.VENUS, 357.0) // 金星在亥宮 27度 exalted.
      .put(Planet.MARS, 298.0) // 火星在丑宮 28度 exalted.
      .put(Planet.JUPITER, 105.0) // 木星在未宮 15度 exalted.
      .put(Planet.SATURN, 201.0) // 土星在辰宮 21度 exalted.
      .put(LunarNode.NORTH_TRUE, 63.0) //北交點在 申宮 03度 exalted.
      .put(LunarNode.NORTH_MEAN, 63.0) //北交點在 申宮 03度 exalted.
      .put(LunarNode.SOUTH_TRUE, 243.0) //南交點在 寅宮 03度 exalted.
      .put(LunarNode.SOUTH_MEAN, 243.0) //南交點在 寅宮 03度 exalted.
      .build()

    /*
     private static Map<Point , Double> starExaltationMap = Collections.synchronizedMap(new HashMap<Point , Double>());
     static
     {
       starExaltationMap.put(Planet.SUN    ,  19.0); // 太陽在戌宮 19度 exalted.
       starExaltationMap.put(Planet.MOON   ,  33.0); // 月亮在酉宮 03度 exalted.
       starExaltationMap.put(Planet.MERCURY, 165.0); // 水星在巳工 15度 exalted.
       starExaltationMap.put(Planet.VENUS  , 357.0); // 金星在亥宮 27度 exalted.
       starExaltationMap.put(Planet.MARS   , 298.0); // 火星在丑宮 28度 exalted.
       starExaltationMap.put(Planet.JUPITER, 105.0); // 木星在未宮 15度 exalted.
       starExaltationMap.put(Planet.SATURN , 201.0); // 土星在辰宮 21度 exalted.
       starExaltationMap.put(LunarNode.NORTH_TRUE ,  63.0); //北交點在 申宮 03度 exalted.
       starExaltationMap.put(LunarNode.SOUTH_TRUE , 243.0); //南交點在 寅宮 03度 exalted.
     }
     */

    /**
     * 放星體在黃道帶上幾度得到 Fall (落 , -4) 的度數
     * 前面 starExaltationMap 中，每個星體的度數 +180度即為「落」
     */
    private val starFallMap = HashMap<Point, Double>()

    init {
      for (eachPoint in starExaltationMap.keys)
        starFallMap.put(eachPoint, Utils.getNormalizeDegree(starExaltationMap[eachPoint]!! + 180))
    }

    /** 星座 + 強弱度 的組合 key , 中間以減號 (-) 串接  */
    private fun getCompositeKey(sign: ZodiacSign, dignity: Dignity): String {
      return sign.toString() + '-' + dignity.toString()
    }

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

    /** key 為 Sign-Dignity , 中間以 '-' 串接  */


    init {
//      /** 設定 Rulership (旺 , +5)  */
//      essentialDignitiesMap.put(getCompositeKey(ARIES, RULER), Planet.MARS)
//      essentialDignitiesMap.put(getCompositeKey(TAURUS, RULER), Planet.VENUS)
//      essentialDignitiesMap.put(getCompositeKey(GEMINI, RULER), Planet.MERCURY)
//      essentialDignitiesMap.put(getCompositeKey(CANCER, RULER), Planet.MOON)
//      essentialDignitiesMap.put(getCompositeKey(LEO, RULER), Planet.SUN)
//      essentialDignitiesMap.put(getCompositeKey(VIRGO, RULER), Planet.MERCURY)
//      essentialDignitiesMap.put(getCompositeKey(LIBRA, RULER), Planet.VENUS)
//      essentialDignitiesMap.put(getCompositeKey(SCORPIO, RULER), Planet.MARS)
//      essentialDignitiesMap.put(getCompositeKey(SAGITTARIUS, RULER), Planet.JUPITER)
//      essentialDignitiesMap.put(getCompositeKey(CAPRICORN, RULER), Planet.SATURN)
//      essentialDignitiesMap.put(getCompositeKey(AQUARIUS, RULER), Planet.SATURN)
//      essentialDignitiesMap.put(getCompositeKey(PISCES, RULER), Planet.JUPITER)
//
//      /**  設定 Detriment (陷 , -5) , 其值為對沖星座之 Ruler  */
//      essentialDignitiesMap.put(getCompositeKey(ARIES, DETRIMENT), essentialDignitiesMap[getCompositeKey(ARIES.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(TAURUS, DETRIMENT), essentialDignitiesMap[getCompositeKey(TAURUS.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(GEMINI, DETRIMENT), essentialDignitiesMap[getCompositeKey(GEMINI.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(CANCER, DETRIMENT), essentialDignitiesMap[getCompositeKey(CANCER.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(LEO, DETRIMENT), essentialDignitiesMap[getCompositeKey(LEO.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(VIRGO, DETRIMENT), essentialDignitiesMap[getCompositeKey(VIRGO.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(LIBRA, DETRIMENT), essentialDignitiesMap[getCompositeKey(LIBRA.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(SCORPIO, DETRIMENT), essentialDignitiesMap[getCompositeKey(SCORPIO.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(SAGITTARIUS, DETRIMENT), essentialDignitiesMap[getCompositeKey(SAGITTARIUS.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(CAPRICORN, DETRIMENT), essentialDignitiesMap[getCompositeKey(CAPRICORN.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(AQUARIUS, DETRIMENT), essentialDignitiesMap[getCompositeKey(AQUARIUS.oppositeSign, RULER)])
//      essentialDignitiesMap.put(getCompositeKey(PISCES, DETRIMENT), essentialDignitiesMap[getCompositeKey(PISCES.oppositeSign, RULER)])
    }
  }

}
