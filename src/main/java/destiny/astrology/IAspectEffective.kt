/**
 * Created by smallufo at 2008/11/6 下午 5:52:48
 */
package destiny.astrology

import java.util.*

/** 一個星盤當中，兩顆星體，是否形成某交角  */
interface IAspectEffective {

  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道帶上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道帶上的角度
   * @return 是否形成有效交角
   */
  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Boolean

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, vararg aspects: Aspect): Boolean {
    return isEffective(p1, deg1, p2, deg2, Arrays.asList(*aspects))
  }

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspects: Collection<Aspect>): Boolean {
    return aspects.any { isEffective(p1, deg1, p2, deg2, it) }
  }
}
