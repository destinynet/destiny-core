/**
 * Created by smallufo on 2021-05-14.
 */
package destiny.core.astrology

import destiny.core.IGmtJulDay
import destiny.core.calendar.GmtJulDay
import java.io.Serializable
import kotlin.math.abs


/**
 * 具備 發生時間 [gmtJulDay] 的交角資料
 */
interface IAngleData : IPointAnglePattern , IGmtJulDay {

  /** 何時 */
  override val gmtJulDay : GmtJulDay

  fun toAspectData(): AspectData? {
    return Aspect.getAspect(angle)?.let { aspect ->
      AspectData.of(this.points, aspect , abs(aspect.degree - angle), null , null , gmtJulDay)
      //AspectData(this.points, null , abs(aspect.degree - angle) , null ,null)
      //AspectData(this, null, abs(aspect.degree - angle))
    }
  }
}

/**
 * 存放星體交角度數的資料結構
 * */
data class AngleData(

  private val pointAnglePattern: IPointAnglePattern,

  /** 何時發生 */
  override val gmtJulDay: GmtJulDay) : IAngleData, IPointAnglePattern by pointAnglePattern, Serializable {

  constructor(p1: AstroPoint, p2: AstroPoint, angle: Double, gmtJulDay: GmtJulDay) : this(PointAnglePattern.of(setOf(p1, p2), angle), gmtJulDay)
}
