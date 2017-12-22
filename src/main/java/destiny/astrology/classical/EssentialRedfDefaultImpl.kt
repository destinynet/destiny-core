/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 3:50:48
 */
package destiny.astrology.classical

import destiny.astrology.*
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.getZodiacSign
import destiny.astrology.classical.Dignity.*
import java.io.Serializable

/**
 * 取得星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail
 * 內定實作為 托勒密 Ptolomy 表格
 */
class EssentialRedfDefaultImpl(private val rulerImpl: IRuler,
                               private val detrimentImpl: IDetriment) : IEssentialRedf, Serializable {


  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 null ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 null
   */
  override fun getPointOld(sign: ZodiacSign, dignity: Dignity): Point? {
    return when (dignity) {
    /** 旺 , +5 */
      RULER -> rulerImpl.getPoint(sign)
    /** 廟 , +4  */
      EXALTATION -> findPoint(sign, starExaltationMap) // nullable
    /** 落 , -4  */
      FALL -> findPoint(sign, starFallMap) // nullable
    /** 陷 , -5 */
      DETRIMENT -> detrimentImpl.getPoint(sign)
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
    return findPoint(sign, starExaltationMap)?.let { point -> PointDegree(point, starExaltationMap[point]!!) }
  }

  /**
   * 取得在此星座得到「Fall , 落 -4」的星體及度數
   */
  fun getFallStarDegree(sign: ZodiacSign): PointDegree? {
    return findPoint(sign, starFallMap)?.let { point -> PointDegree(point, starFallMap[point]!!) }
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
      , LunarNode.NORTH_TRUE to 63.0  //北交點在 申宮 03度 exalted.
      , LunarNode.NORTH_MEAN to 63.0  //北交點在 申宮 03度 exalted.
      , LunarNode.SOUTH_TRUE to 243.0  //南交點在 寅宮 03度 exalted.
      , LunarNode.SOUTH_MEAN to 243.0  //南交點在 寅宮 03度 exalted.
    )


    /**
     * 放星體在黃道帶上幾度得到 Fall (落 , -4) 的度數
     * 前面 starExaltationMap 中，每個星體的度數 +180度即為「落」
     */

    private val starFallMap = starExaltationMap.keys.map {
      it to Utils.getNormalizeDegree(starExaltationMap[it]!! + 180)
    }.toMap()




  }

}
