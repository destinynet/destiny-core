/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IAspectData
import destiny.core.calendar.GmtJulDay


data class ProgressedAspect(
  val progressedPoint: AstroPoint,
  val natalPoint: AstroPoint,
  override val angle: Double,
  override val type: IAspectData.Type,
  override val score: Double?
) : IAspectData {

  override val points: Set<AstroPoint> = setOf(progressedPoint, natalPoint)

  override val gmtJulDay: GmtJulDay? = null

  override lateinit var aspect: Aspect //= Aspect.getAspect(angle)!!

  init {

    require(Aspect.getAspect(angle) != null)

    println("aspect = ${Aspect.getAspect(angle)}")
    this.aspect = Aspect.getAspect(angle)!!
  }







  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProgressedAspect) return false

    if (progressedPoint != other.progressedPoint) return false
    if (natalPoint != other.natalPoint) return false
    if (angle != other.angle) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = progressedPoint.hashCode()
    result = 31 * result + natalPoint.hashCode()
    result = 31 * result + angle.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }


}

interface IProgressionModel {
  val natalTime: GmtJulDay
  val angleDataSet: Set<ProgressedAspect>
}
