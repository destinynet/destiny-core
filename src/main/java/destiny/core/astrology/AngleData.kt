/**
 * Created by smallufo on 2021-05-14.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import java.io.Serializable
import kotlin.math.abs

interface IAngleData {
  /** 兩顆星體  */
  val points: Set<AstroPoint>

  /** 交角幾度 */
  val angle: Double

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
  /** 兩顆星體  */
  override val points: Set<AstroPoint>,
  /** 交角幾度 */
  override val angle: Double ,
  /** 何時發生 */
  override val gmtJulDay: GmtJulDay?) : IAngleData , Serializable {

  constructor(p1: AstroPoint, p2: AstroPoint, angle: Double, gmtJulDay: GmtJulDay) : this(sortedSetOf(pointComp, p1, p2), angle, gmtJulDay)

  companion object {
    val pointComp = AstroPointComparator()
  }
}
