/**
 * Created by smallufo on 2021-05-14.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import java.io.Serializable
import kotlin.math.abs

interface IPointAspectPattern : IAstroPattern {
  /** 兩顆星體  */
  val points: Set<AstroPoint>

  /** 交角幾度 */
  val angle: Double
}

data class PointAspectPattern internal constructor(
  override val points: Set<AstroPoint>,
  override val angle: Double
) : IPointAspectPattern, Serializable {
  companion object {
    fun of(points: Set<AstroPoint>, angle: Double): PointAspectPattern {
      val (p1, p2) = points.iterator().let { it.next() to it.next() }
      return PointAspectPattern(sortedSetOf(AstroPointComparator(), p1, p2), angle)
    }
  }
}


interface IAngleData : IPointAspectPattern {

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

  private val pointAspectPattern: PointAspectPattern,

  /** 何時發生 */
  override val gmtJulDay: GmtJulDay?) : IAngleData , IPointAspectPattern by pointAspectPattern, Serializable {

  constructor(p1: AstroPoint, p2: AstroPoint, angle: Double, gmtJulDay: GmtJulDay) : this(PointAspectPattern.of(setOf(p1, p2), angle), gmtJulDay)
}
