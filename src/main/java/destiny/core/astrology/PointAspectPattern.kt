package destiny.core.astrology

import destiny.core.toString
import destiny.tools.AlignTools
import java.util.*
import kotlin.math.abs

/** 兩顆（可能相同）星體的交角 */
interface IPointAspectPattern : IPointAnglePattern, Comparable<IPointAspectPattern> {

  /** 兩星所形成的交角 */
  val aspect: Aspect
    get() = Aspect.getAspect(angle)!!

  /** 交會型態 : 接近 or 分離 */
  val type: Type?

  /** orb 不列入 equals / hashCode 計算  */
  val orb: Double
    get() = abs(aspect.degree - angle)

  /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
  val score: Double?

  enum class Type {
    APPLYING,
    SEPARATING
  }

  override fun compareTo(other: IPointAspectPattern): Int {
    val (thisP0, thisP1) = points.iterator()
      .let {
        it.next() to it.next()
      }

    val (thatP0, thatP1) = other.points.iterator()
      .let {
        it.next() to it.next()
      }

    return if (thisP0.javaClass.name == thatP0.javaClass.name && thisP0 == thatP0) {
      if (thisP1.javaClass.name == thatP1.javaClass.name) (thisP1 as Comparable<AstroPoint>).compareTo(thatP1)
      else pointComp.compare(thisP1, thatP1)
    } else {
      pointComp.compare(thisP0, thatP0)
    }
  }

  fun brief(): String {
    val typeString = type?.toString()?.take(1) ?: "?"
    return StringBuilder("[$typeString] [${points.joinToString(", ") { it.toString(Locale.TRADITIONAL_CHINESE) }}] $aspect 誤差 ${AlignTools.leftPad(orb.toString(), 4)}度").apply {
      score?.also { score: Double ->
        val s = (score * 100).toString()
          .take(5)
        append("，得分：$s")
      }
    }
      .toString()
  }

  companion object {
    private val pointComp = AstroPointComparator()
  }
}


/** 兩顆（可能相同）星體的交角 */
data class PointAspectPattern internal constructor(override val points: List<AstroPoint>,
                                                   override val angle: Double,
                                                   override val type: IPointAspectPattern.Type?,
                                                   override val orb: Double,
                                                   override val score: Double? = null) : IPointAspectPattern {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PointAspectPattern) return false

    if (points != other.points) return false
    if (angle != other.angle) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = points.hashCode()
    result = 31 * result + angle.hashCode()
    result = 31 * result + (type?.hashCode() ?: 0)
    return result
  }

  companion object {
    private val pointComp = AstroPointComparator()

    fun of(p1: AstroPoint, p2: AstroPoint, aspect: Aspect, type: IPointAspectPattern.Type?, orb: Double = 0.0, score: Double? = null): PointAspectPattern {
      val points = if (p1 != p2) {
        sortedSetOf(pointComp, p1, p2).toList()
      } else {
        listOf(p1, p2)
      }
      return PointAspectPattern(points, aspect.degree, type, orb, score)
    }
  }


}
