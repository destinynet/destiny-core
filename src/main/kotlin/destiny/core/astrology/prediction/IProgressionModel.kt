/**
 * Created by smallufo on 2022-08-03.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IPointAspectPattern
import destiny.core.calendar.GmtJulDay
import java.io.Serializable


interface IProgressedAspect : IPointAspectPattern {
  val progressedPoint: AstroPoint
  val natalPoint: AstroPoint

  override val points: List<AstroPoint>
    get() = listOf(progressedPoint, natalPoint)
}

data class ProgressedAspect(override val progressedPoint: AstroPoint,
                            override val natalPoint: AstroPoint,
                            override val aspect: Aspect,
                            override val orb: Double,
                            override val type: IPointAspectPattern.Type,
                            override val score: Double?) : IProgressedAspect {

  override val angle: Double = aspect.degree

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProgressedAspect) return false

    if (progressedPoint != other.progressedPoint) return false
    if (natalPoint != other.natalPoint) return false
    if (aspect != other.aspect) return false
    return type == other.type
  }

  override fun hashCode(): Int {
    var result = progressedPoint.hashCode()
    result = 31 * result + natalPoint.hashCode()
    result = 31 * result + aspect.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}

enum class ProgressionType {
  TRANSIT, SECONDARY, TERTIARY, MINOR
}

interface ITransitModel : Serializable {
  val natalTime: GmtJulDay
  val viewTime: GmtJulDay
}

interface IProgressionModel : ITransitModel {
  val type : ProgressionType
  val convergentTime: GmtJulDay
  val progressedAspects: Set<IProgressedAspect>
  val progressedAspectsByScore : List<IProgressedAspect>
    get() {
      return progressedAspects.asSequence().sortedByDescending { it.score }.toList()
    }
}

data class ProgressionModel(override val type: ProgressionType,
                            override val natalTime: GmtJulDay,
                            override val viewTime: GmtJulDay,
                            override val convergentTime: GmtJulDay,
                            override val progressedAspects: Set<IProgressedAspect>) : IProgressionModel
