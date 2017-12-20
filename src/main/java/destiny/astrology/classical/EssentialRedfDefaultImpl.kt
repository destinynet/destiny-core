/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 3:50:48
 */
package destiny.astrology.classical

import destiny.astrology.*
import destiny.astrology.DayNight.DAY
import destiny.astrology.DayNight.NIGHT
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.*
import java.io.Serializable

/**
 * 取得星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail
 * 內定實作為 托勒密表格
 */
class EssentialRedfDefaultImpl : IEssentialRedf, Serializable {

  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 null ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 null
   */
  override fun getPoint(sign: ZodiacSign, dignity: Dignity): Point? {
    return when (dignity) {
    /** 廟 , +4  */
      EXALTATION -> findPoint(sign, starExaltationMap) // nullable
    /** 落 , -4  */
      FALL -> findPoint(sign, starFallMap) // nullable
      else -> rulerDetrimentMap[(sign to dignity)] // not null
    }
  }

  private fun findPoint(sign: ZodiacSign, map: Map<Point, Double>): Point? {
    return map.entries
      .filter { (_, value) -> sign === getZodiacSign(value) }
      .map { entry -> entry.key }
      .firstOrNull()
  }


  /**
   * 取得「日夜區分版本」的 [RULER]
   */
  fun getRulerByDayNight(sign: ZodiacSign , dayNight: DayNight) : Planet? {
    return rulerDayNightMap[(sign to dayNight)]
  }

  /**
   * 取得此行星在日、夜 是什麼星座的 [RULER]
   */
  fun getRulingByDayNight(planet: Planet , dayNight: DayNight) : ZodiacSign? {
    return planetDayNightRulerMap[(planet to dayNight)]
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
       SUN to 19.0  // 太陽在戌宮 19度 exalted.
      , MOON to 33.0  // 月亮在酉宮 03度 exalted.
      , MERCURY to 165.0  // 水星在巳宮 15度 exalted.
      , VENUS to 357.0  // 金星在亥宮 27度 exalted.
      , MARS to 298.0  // 火星在丑宮 28度 exalted.
      , JUPITER to 105.0  // 木星在未宮 15度 exalted.
      , SATURN to 201.0  // 土星在辰宮 21度 exalted.
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


    /** 不考量「日、夜」的 ruler */
    private val rulerMap = mapOf<Pair<ZodiacSign, Dignity>, Planet>(
      (ARIES       to RULER) to MARS,
      (TAURUS      to RULER) to VENUS,
      (GEMINI      to RULER) to MERCURY,
      (CANCER      to RULER) to MOON,
      (LEO         to RULER) to SUN,
      (VIRGO       to RULER) to MERCURY,
      (LIBRA       to RULER) to VENUS,
      (SCORPIO     to RULER) to MARS,
      (SAGITTARIUS to RULER) to JUPITER,
      (CAPRICORN   to RULER) to SATURN,
      (AQUARIUS    to RULER) to SATURN,
      (PISCES      to RULER) to JUPITER
    )

    /** 不考量「日、夜」的  [RULER] (旺, +５) / [DETRIMENT]  (陷, -5) Map */
    private val rulerDetrimentMap: Map<Pair<ZodiacSign, Dignity>, Planet> = rulerMap.toMutableMap().let {
      ZodiacSign.values().map { sign ->
        val key = (sign to DETRIMENT)
        val value = rulerMap[sign.oppositeSign to RULER]!!
        it.put(key, value)
      }
      it
    }.toMap()


    /**
     * 考量日夜的 rulerMap , 參考表格 : https://imgur.com/a/bZ6ij
     * 讀作 : 什麼星座的日/夜 的 ruler 是誰(maybe null)
     * */
    private val rulerDayNightMap = mapOf<Pair<ZodiacSign , DayNight> , Planet> (
      (ARIES to DAY) to MARS ,
      (TAURUS to NIGHT) to VENUS,
      (GEMINI to DAY) to MERCURY,
      (CANCER to DAY) to MOON,
      (CANCER to NIGHT) to MOON,
      (LEO to DAY) to SUN,
      (LEO to NIGHT) to SUN,
      (VIRGO to NIGHT) to MERCURY,
      (LIBRA to DAY) to VENUS,
      (SCORPIO to NIGHT) to MARS,
      (SAGITTARIUS to DAY) to JUPITER,
      (CAPRICORN to NIGHT) to SATURN,
      (AQUARIUS to DAY) to SATURN,
      (PISCES to NIGHT) to JUPITER
    )

    /**
     * (火星, DAY) ==> 牡羊
     * (火星, NIGHT) ==> 天蠍
     *
     * (金星, NIGHT) ==> 金牛
     * (金星, DAY) ==> 天秤
     *
     * (水星, DAY) ==> 雙子
     * (水星, NIGHT) ==> 處女
     *
     * (月亮, DAY) ==> 巨蟹
     * (月亮, NIGHT) ==> 巨蟹
     *
     * (太陽, DAY) ==> 獅子
     * (太陽, NIGHT) ==> 獅子
     *
     * (木星, DAY) ==> 射手
     * (木星, NIGHT) ==> 雙魚
     *
     * (土星, NIGHT) ==> 摩羯
     * (土星, DAY) ==> 水瓶
     */
    private val planetDayNightRulerMap: Map<Pair<Planet, DayNight>, ZodiacSign> = rulerDayNightMap
      .entries
      .map { (sign_to_DN,planet) -> planet to sign_to_DN }
      .groupBy { (planet ,sign_to_DN) -> planet }
      .flatMap { (planet,sign_to_DN) -> sign_to_DN }
      .map { (planet , sign_to_DN) -> (planet to sign_to_DN.second) to sign_to_DN.first }
      .toMap()

  }

}
