/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:34:57
 */
package destiny.astrology

import destiny.tools.AlignTools
import mu.KotlinLogging
import java.io.Serializable

/** 存放星體交角的資料結構  */
data class HoroscopeAspectData(val p1: Point,
                               val p2: Point,
                               /** 兩星所形成的交角  */
                               val aspect: Aspect,
                               /** orb 不列入 equals / hashCode 計算  */
                               val orb: Double = 0.0 ,
                               /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
                               val score:Double? = null) : Comparable<HoroscopeAspectData>, Serializable {
  private val pointComp = PointComparator()

  /** 存放形成交角的兩顆星體  */
  val points = setOf(p1 , p2)

  private val logger = KotlinLogging.logger {  }

  init {
    val set = sortedSetOf(pointComp).apply {
      add(p1)
      add(p2)
    }

    if (set.size <= 1) {
      logger.warn("twoPoints size = {} , p1 = {} ({}) , p2 = {} ({}) . equals ? {}",
                  set.size, p1, p1.hashCode(), p2, p2.hashCode(), p1 == p2)
    }
  }

  override fun toString(): String {
    return "$points $aspect 誤差 ${AlignTools.leftPad(orb.toString(), 4)}度"
  }


  /** 傳入一個 point , 取得另一個 point , 如果沒有，則傳回 null  */
  fun getAnotherPoint(thisPoint: Point): Point? {
    return points
      .takeIf { it.contains(thisPoint) }
      ?.minus(thisPoint)
      ?.firstOrNull()
  }

  override fun compareTo(other: HoroscopeAspectData): Int {
    val it1 = points.iterator()
    val it2 = other.points.iterator()
    val thisP0 = it1.next()
    val thisP1 = it1.next()
    val thatP0 = it2.next()
    val thatP1 = it2.next()

    return if (thisP0.javaClass.name == thatP0.javaClass.name && thisP0 == thatP0) {
      if (thisP1.javaClass.name == thatP1.javaClass.name)
        (thisP1 as Comparable<Point>).compareTo(thatP1)
      else
        pointComp.compare(thisP1, thatP1)
    } else {
      pointComp.compare(thisP0, thatP0)
    }

  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is HoroscopeAspectData) return false

    if (aspect != other.aspect) return false
    if (points != other.points) return false

    return true
  }

  override fun hashCode(): Int {
    var result = aspect.hashCode()
    result = 31 * result + points.hashCode()
    return result
  }


}
