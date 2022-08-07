/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:34:57
 */
package destiny.core.astrology

import destiny.core.astrology.IPointAspectPattern.Type
import destiny.core.calendar.GmtJulDay
import java.io.Serializable


interface IAspectData : IPointAspectPattern, IAngleData

/**
 * 存放兩顆「不同」星體交角的資料結構
 * */
data class AspectData(val angleData: IAngleData,
                      /** 交會型態 : 接近 or 分離 */
                      override val type: Type? = null,
                      /** orb 不列入 equals / hashCode 計算  */
                      override val orb: Double = 0.0,
                      /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
                      override val score: Double? = null) : IAspectData, IAngleData by angleData, Serializable {

  private constructor(
    /** 存放形成交角的兩顆「不同」星體  */
    points: Set<AstroPoint>,
    /** 兩星所形成的交角 */
    aspect: Aspect,
    /** 交會型態 : 接近 or 分離 */
    type: Type? = null,
    /** orb 不列入 equals / hashCode 計算  */
    orb: Double = 0.0,
    /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
    score: Double? = null,
    gmtJulDay: GmtJulDay
  ) : this(
    AngleData(PointAnglePattern.of(points, aspect.degree), gmtJulDay),
    type, orb, score
  )


  init {
    require(points.size == 2) { "INCORRECT points"}
  }

//  override fun compareTo(other: AspectData): Int {
//
//    val (thisP0, thisP1) = points.iterator()
//      .let {
//        it.next() to it.next()
//      }
//
//    val (thatP0, thatP1) = other.points.iterator()
//      .let {
//        it.next() to it.next()
//      }
//
//    return if (thisP0.javaClass.name == thatP0.javaClass.name && thisP0 == thatP0) {
//      if (thisP1.javaClass.name == thatP1.javaClass.name) (thisP1 as Comparable<AstroPoint>).compareTo(thatP1)
//      else pointComp.compare(thisP1, thatP1)
//    } else {
//      pointComp.compare(thisP0, thatP0)
//    }
//
//  }

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
    private val pointComp = AstroPointComparator()

    fun of(p1: AstroPoint, p2: AstroPoint, aspect: Aspect, orb: Double, score: Double? = null, type: Type? = null, gmtJulDay: GmtJulDay): AspectData {
      val points = if (p1 != p2) {
        sortedSetOf(pointComp, p1, p2)
      } else {
        listOf(p1, p2).toSet()
      }

      return AspectData(points, aspect, type, orb, score, gmtJulDay)
    }

  }


}
