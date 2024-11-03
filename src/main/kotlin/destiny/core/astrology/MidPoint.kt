/**
 * Created by smallufo on 2024-11-04.
 */
package destiny.core.astrology


data class MidPoint(val points: Set<AstroPoint>, val degree: ZodiacDegree) {
  init {
    require(points.size == 2) {
      "needs 2 astro points"
    }
  }

  val p1: AstroPoint by lazy {
    points.sortedWith(AstroPointComparator).first()
  }

  val p2: AstroPoint by lazy {
    points.sortedWith(AstroPointComparator).last()
  }
}
