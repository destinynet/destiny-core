/**
 * Created by smallufo at 2008/11/6 下午 5:52:48
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree

/** 一個星盤當中，兩顆星體，是否形成某交角  */
interface IAspectEffective {

  val applicableAspects : Set<Aspect>

  /**
   * 取得這兩星是否形成此有效交角
   * 如果形成交角 , 則傳回 error(orb) 角度 , 以及評分
   * 若無效，則傳回 null
   */
  fun getEffectiveErrorAndScore(p1: Point, deg1: ZodiacDegree, p2: Point, deg2: ZodiacDegree, aspect: Aspect): Pair<Double, Double>?

  /**
   * 承上 , 另一種寫法
   */
  fun getEffectiveErrorAndScore(p1: Point, p2: Point, posMap: Map<Point, IPos>, aspect: Aspect): Pair<Double, Double>? {
    return getEffectiveErrorAndScore(p1, posMap.getValue(p1).lngDeg, p2, posMap.getValue(p2).lngDeg, aspect)
  }


  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道帶上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道帶上的角度
   * @return 是否形成有效交角
   */
  fun isEffective(p1: Point, deg1: ZodiacDegree, p2: Point, deg2: ZodiacDegree, aspect: Aspect): Boolean

  fun isEffective(p1: Point, p2: Point, posMap: Map<Point, IPos>, aspect: Aspect): Boolean {
    return isEffective(p1, posMap.getValue(p1).lngDeg, p2, posMap.getValue(p2).lngDeg, aspect)
  }

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, vararg aspects: Aspect): Boolean {
    return isEffective(p1, deg1, p2, deg2, listOf(*aspects))
  }

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspects: Collection<Aspect>): Boolean {
    return aspects.any { isEffective(p1, deg1.toZodiacDegree(), p2, deg2.toZodiacDegree(), it) }
  }



}
