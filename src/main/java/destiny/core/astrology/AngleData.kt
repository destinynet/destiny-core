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

data class PointAspectPattern(override val points: Set<AstroPoint>,
                              override val angle: Double) : IPointAspectPattern, Serializable


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

  constructor(p1: AstroPoint, p2: AstroPoint, angle: Double, gmtJulDay: GmtJulDay) : this(PointAspectPattern(sortedSetOf(pointComp, p1, p2), angle), gmtJulDay)

  companion object {
    val pointComp = AstroPointComparator()
  }
}
