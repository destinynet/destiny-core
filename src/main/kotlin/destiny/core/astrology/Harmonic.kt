/**
 * Created by smallufo on 2025-09-04.
 */
package destiny.core.astrology

import destiny.tools.serializers.IZodiacDegreeTwoDecimalSerializer
import kotlinx.serialization.Serializable


@Serializable
data class Harmonic(
  val n: Int,
  val aspects: List<IPointAspectPattern>,
  val starPosMap: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>
)
