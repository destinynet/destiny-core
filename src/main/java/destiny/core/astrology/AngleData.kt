/**
 * Created by smallufo on 2021-05-14.
 */
package destiny.core.astrology

import java.io.Serializable
import kotlin.math.abs

interface IAngleData {
  /** 兩顆星體  */
  val points: Set<Point>

  /** 交角幾度 */
  val angle: Double

  /** 何時 */
  val gmtJulDay : Double?

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
  override val points: Set<Point>,
  /** 交角幾度 */
  override val angle: Double ,
  /** 何時發生 */
  override val gmtJulDay: Double?) : IAngleData , Serializable {

  constructor(p1: Point, p2: Point, angle: Double, gmtJulDay: Double) : this(sortedSetOf(pointComp, p1, p2), angle, gmtJulDay)

  companion object {
    val pointComp = PointComparator()
  }
}
