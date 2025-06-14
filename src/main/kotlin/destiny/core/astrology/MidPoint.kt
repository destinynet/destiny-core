/**
 * Created by smallufo on 2024-11-04.
 */
package destiny.core.astrology

import kotlinx.serialization.Serializable

interface IMidPoint {
  val points: Set<AstroPoint>
  val degree: IZodiacDegree
  val house: Int
  val p1: AstroPoint
    get() = points.sortedWith(AstroPointComparator).first()
  val p2: AstroPoint
    get() = points.sortedWith(AstroPointComparator).last()
}

@Serializable
data class MidPoint(override val points: Set<AstroPoint>, override val degree: ZodiacDegree, override val house: Int) : IMidPoint {
  init {
    require(points.size == 2) {
      "A midpoint needs exactly 2 astro points"
    }
  }
}

interface IMidPointWithFocal : IMidPoint {
  val focal: AstroPoint
  val orb: Double
}

@Serializable
data class MidPointWithFocal(private val midPoint: MidPoint, override val focal: AstroPoint, override val orb: Double) : IMidPointWithFocal, IMidPoint by midPoint {
  init {
    require(!points.contains(focal)) {
      "Focal point cannot be one of the points defining the midpoint"
    }
  }
}
