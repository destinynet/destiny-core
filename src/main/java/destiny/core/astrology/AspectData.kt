/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:34:57
 */
package destiny.core.astrology

import destiny.tools.AlignTools
import java.io.Serializable


interface IAspectData : IAngleData {

  /** 兩星所形成的交角 */
  val aspect: Aspect
  val type: AspectData.Type?

  /** orb 不列入 equals / hashCode 計算  */
  val orb: Double

  /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
  val score: Double?
}

/**
 * 存放星體交角的資料結構
 * */
data class AspectData(
  /** 存放形成交角的兩顆星體  */
  val points: Set<Point>,
  /** 兩星所形成的交角 */
  val aspect: Aspect,
  val type: Type? = null,
  /** orb 不列入 equals / hashCode 計算  */
  val orb: Double = 0.0,
  /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
  val score: Double? = null) : Comparable<AspectData>, Serializable {



  enum class Type {
    APPLYING, SEPARATING
  }

  constructor(p1: Point, p2: Point, aspect: Aspect, type: Type? = null, orb: Double = 0.0, score: Double? = null) :
    this(sortedSetOf(pointComp, p1, p2), aspect, type, orb, score)

  constructor(p1: Point, p2: Point, aspect: Aspect, orb: Double, score: Double? = null, type: Type? = null) :
    this(sortedSetOf(pointComp, p1, p2), aspect, type, orb, score)

  override fun toString(): String {
    val typeString = type?.toString()?.substring(0, 1) ?: "?"
    return StringBuilder("[$typeString] $points $aspect 誤差 ${AlignTools.leftPad(orb.toString(), 4)}度").apply {
      score?.also { score: Double ->
        val s = (score * 100).toString().take(5)
        append("，得分：$s")
      }
    }.toString()
  }


  /** 傳入一個 point , 取得另一個 point , 如果沒有，則傳回 null  */
  fun getAnotherPoint(thisPoint: Point): Point? {
    return points
      .takeIf { it.contains(thisPoint) }
      ?.minus(thisPoint)
      ?.firstOrNull()
  }

  override fun compareTo(other: AspectData): Int {

    val (thisP0, thisP1) = points.iterator().let {
      it.next() to it.next()
    }

    val (thatP0, thatP1) = other.points.iterator().let {
      it.next() to it.next()
    }

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
    if (other !is AspectData) return false

    if (points != other.points) return false
    if (aspect != other.aspect) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = points.hashCode()
    result = 31 * result + aspect.hashCode()
    result = 31 * result + (type?.hashCode() ?: 0)
    return result
  }


  companion object {
    val pointComp = PointComparator()
  }


}
