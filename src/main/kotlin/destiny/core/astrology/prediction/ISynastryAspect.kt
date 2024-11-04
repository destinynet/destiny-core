package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IMidPointWithFocal
import destiny.core.astrology.IPointAspectPattern

interface ISynastryAspect : IPointAspectPattern {
  val outerPoint: AstroPoint
  val innerPoint: AstroPoint
  val outerPointHouse: Int?
  val innerPointHouse: Int?

  override val points: List<AstroPoint>
    get() = listOf(outerPoint, innerPoint)
}

data class SynastryAspect(
  override val outerPoint: AstroPoint,
  override val innerPoint: AstroPoint,
  override val outerPointHouse: Int?,
  override val innerPointHouse: Int?,
  override val aspect: Aspect,
  override val orb: Double,
  override val type: IPointAspectPattern.Type?,
  override val score: Double?
) : ISynastryAspect {

  override val angle: Double = aspect.degree
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SynastryAspect) return false

    if (outerPoint != other.outerPoint) return false
    if (innerPoint != other.innerPoint) return false
    if (outerPointHouse != other.outerPointHouse) return false
    if (innerPointHouse != other.innerPointHouse) return false
    if (aspect != other.aspect) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = outerPoint.hashCode()
    result = 31 * result + innerPoint.hashCode()
    result = 31 * result + (outerPointHouse ?: 0)
    result = 31 * result + (innerPointHouse ?: 0)
    result = 31 * result + aspect.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}

data class MidPointFocalAspect(val outer : IMidPointWithFocal, val inner : IMidPointWithFocal,
                               override val aspect: Aspect,
                               override val orb: Double) : ISynastryAspect {
  override val points: List<AstroPoint> = listOf(outer.focal, inner.focal)
  override val outerPoint: AstroPoint = outer.focal
  override val innerPoint: AstroPoint = inner.focal

  override val type: IPointAspectPattern.Type? = null
  override val outerPointHouse: Int? = null
  override val innerPointHouse: Int? = null
  override val angle: Double = aspect.degree
  override val score: Double? = null
}
