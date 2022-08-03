/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IAspectData
import destiny.core.calendar.GmtJulDay
import java.io.Serializable


interface IProgressedAspect : IAspectData {
  val progressedPoint: AstroPoint
  val natalPoint: AstroPoint

  override val points: Set<AstroPoint>
    get() = setOf(progressedPoint, natalPoint)
}

data class ProgressedAspect(override val progressedPoint: AstroPoint,
                            override val natalPoint: AstroPoint,
                            override val aspect: Aspect,
                            override val orb: Double,
                            override val type: IAspectData.Type,
                            override val score: Double?) : IProgressedAspect {

  override val angle: Double = aspect.degree

  override val gmtJulDay: GmtJulDay? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProgressedAspect) return false

    if (progressedPoint != other.progressedPoint) return false
    if (natalPoint != other.natalPoint) return false
    if (aspect != other.aspect) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = progressedPoint.hashCode()
    result = 31 * result + natalPoint.hashCode()
    result = 31 * result + aspect.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }


}

interface IProgressionModel : Serializable {
  val natalTime: GmtJulDay
  val progressionTime: GmtJulDay
  val convergentTime: GmtJulDay
  val progressedAspects: Set<IProgressedAspect>
}

data class ProgressionModel(override val natalTime: GmtJulDay,
                            override val progressionTime: GmtJulDay,
                            override val convergentTime: GmtJulDay,
                            override val progressedAspects: Set<IProgressedAspect>) : IProgressionModel
