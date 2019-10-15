/**
 * Created by smallufo at 2008/11/6 下午 5:52:48
 */
package destiny.astrology

/** 一個星盤當中，兩顆星體，是否形成某交角  */
interface IAspectEffective {

  /**
   * 取得這兩星是否形成此有效交角
   * 若無效，則傳回 null
   * 若有效，則傳回 pair of (orb , score)
   */
  fun getAspectErrorAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect) : Pair<Double , Double>?

  /**
   * Triple of (是否形成交角 , error 多少 , 分數幾分)
   */
  fun isEffectiveAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Triple<Boolean , Double , Double>

  fun isEffectiveAndScore(p1: Point, p2: Point, posMap: Map<Point, IPos>, aspect: Aspect): Pair<Boolean , Double> {
    return isEffectiveAndScore(p1 , posMap.getValue(p1).lng , p2 , posMap.getValue(p2).lng , aspect).let { (effective , _ , score) ->
      effective to score
    }
  }

  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道帶上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道帶上的角度
   * @return 是否形成有效交角
   */
  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Boolean

  fun isEffective(p1: Point , p2: Point , posMap: Map<Point, IPos> , aspect: Aspect): Boolean{
    return isEffective(p1 ,  posMap.getValue(p1).lng , p2 , posMap.getValue(p2).lng , aspect)
  }

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, vararg aspects: Aspect): Boolean {
    return isEffective(p1, deg1, p2, deg2, listOf(*aspects))
  }

  fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspects: Collection<Aspect>): Boolean {
    return aspects.any { isEffective(p1, deg1, p2, deg2, it) }
  }
}
