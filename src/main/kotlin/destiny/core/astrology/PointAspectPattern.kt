package destiny.core.astrology

import destiny.core.astrology.IPointAspectPattern.Type
import destiny.core.toString
import destiny.tools.AlignTools
import destiny.tools.Score
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
  val score: Score?

  @Serializable
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
      else AstroPointComparator.compare(thisP1, thatP1)
    } else {
      AstroPointComparator.compare(thisP0, thatP0)
    }
  }

  fun brief(): String {
    val typeString = type?.toString()?.take(1) ?: "?"
    val pointsString = points.joinToString(", ") { it.toString(Locale.TRADITIONAL_CHINESE) }
    val orbString = AlignTools.leftPad(orb.toString(), 4)
    val scoreString = score?.let { " ，得分：${(it.value * 100).toString().take(5)}" } ?: ""

    return "[$typeString] [$pointsString] $aspect 誤差 ${orbString}度$scoreString"
  }

}


/** 兩顆（可能相同）星體的交角 */
@Serializable
data class PointAspectPattern(
  override val points: List<AstroPoint>,
  override val angle: Double,
  @SerialName("patternType")
  override val type: Type?,
  override val orb: Double,
  override val score: Score? = null
) : IPointAspectPattern {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PointAspectPattern) return false

    if (points != other.points) return false
    if (angle != other.angle) return false
    return type == other.type
  }

  override fun hashCode(): Int {
    var result = points.hashCode()
    result = 31 * result + angle.hashCode()
    result = 31 * result + (type?.hashCode() ?: 0)
    return result
  }

  companion object {

    fun of(p1: AstroPoint, p2: AstroPoint, aspect: Aspect, type: Type?, orb: Double = 0.0, score: Score? = null): PointAspectPattern {
      val points = if (p1 != p2) {
        sortedSetOf(AstroPointComparator, p1, p2).toList()
      } else {
        listOf(p1, p2)
      }
      return PointAspectPattern(points, aspect.degree, type, orb, score)
    }
  }


}
