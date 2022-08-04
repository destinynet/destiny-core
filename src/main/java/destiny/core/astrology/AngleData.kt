/**
 * Created by smallufo on 2021-05-14.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.toString
import java.io.Serializable
import java.util.*
import kotlin.math.abs

interface IPointAnglePattern : IAstroPattern {
  /** 兩顆星體  */
  val points: List<AstroPoint>

  /** 交角幾度 */
  val angle: Double
}

data class PointAnglePattern internal constructor(
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
    fun of(points: List<AstroPoint>, angle: Double): PointAnglePattern {
      val (p1, p2) = points.iterator().let { it.next() to it.next() }

      val pointList = if (p1 != p2) {
        sortedSetOf(AstroPointComparator(), p1, p2).toList()
      } else {
        listOf(p1, p2)
      }

      return PointAnglePattern(pointList, angle)
    }
  }
}


interface IAngleData : IPointAnglePattern {

  /** 何時 */
  val gmtJulDay : GmtJulDay?

  fun toAspectData(): AspectData? {
    return Aspect.getAspect(angle)?.let { aspect ->
      AspectData(this, null, abs(aspect.degree - angle))
    }
  }
}

/**
 * 存放星體交角度數的資料結構
 * */
data class AngleData(

  private val pointAnglePattern: PointAnglePattern,

  /** 何時發生 */
  override val gmtJulDay: GmtJulDay?) : IAngleData, IPointAnglePattern by pointAnglePattern, Serializable {

  constructor(p1: AstroPoint, p2: AstroPoint, angle: Double, gmtJulDay: GmtJulDay) : this(PointAnglePattern.of(listOf(p1, p2), angle), gmtJulDay)
}
