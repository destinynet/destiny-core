/**
 * Created by smallufo on 2025-09-04.
 */
package destiny.core.astrology


data class Harmonic(
  val n: Int,
  val aspects: List<IPointAspectPattern>,
  val starPosMap: Map<AstroPoint, IZodiacDegree>
)
