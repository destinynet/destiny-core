package destiny.core.astrology

import destiny.core.toString
import java.io.Serializable
import java.util.*

/** 兩顆（可能相同）星體的度數 */
interface IPointAnglePattern : IAstroPattern {

  /** 兩顆星體 (可能相同)  */
  val points: List<AstroPoint>

  /** 交角幾度 */
  val angle: Double

  fun getAnotherPoint(point: AstroPoint): AstroPoint? {
    return points.takeIf { it.contains(point) }
      ?.minus(point)
      ?.firstOrNull()
  }
}

data class PointAnglePattern(
  override val points: List<AstroPoint>,
  override val angle: Double
) : IPointAnglePattern, Serializable {

  override fun getName(locale: Locale): String {
    return buildString {
      points.iterator().also { iterator ->
        append(iterator.next().toString(locale))
        append(" 與 ")
        append(iterator.next().toString(locale))
      }
      append(" 形成 ")
      append(angle.toInt())
      append("度")
    }
  }
  companion object {
    fun of(points: Set<AstroPoint>, angle: Double): PointAnglePattern {
      val (p1, p2) = points.iterator().let { it.next() to it.next() }

      val pointList = if (p1 != p2) {
        sortedSetOf(AstroPointComparator, p1, p2).toList()
      } else {
        listOf(p1, p2)
      }

      return PointAnglePattern(pointList, angle)
    }
  }
}
