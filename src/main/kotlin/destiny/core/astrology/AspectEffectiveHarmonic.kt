/**
 * Created by smallufo on 2025-09-04.
 */
package destiny.core.astrology

import destiny.tools.Score
import kotlin.math.abs


class AspectEffectiveHarmonic(
  val n: Int,
  private val baseAspectEffective: IAspectEffective
) : IAspectEffective {
  override val applicableAspects: Set<Aspect>
    get() = baseAspectEffective.applicableAspects

  override fun getEffectiveErrorAndScore(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Pair<Double, Score>? {
    val threshold: Double = when (n) {
      2, 3    -> 0.92
      4, 5    -> 0.94
      6, 7, 8 -> 0.96
      9       -> 0.98
      else    -> 0.99
    }

    return baseAspectEffective.getEffectiveErrorAndScore(p1, deg1, p2, deg2, aspect)?.takeIf { (baseOrb, baseScore) -> baseScore.value > threshold }
  }

  override fun isEffective(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Boolean {

    return baseAspectEffective.getEffectiveErrorAndScore(p1, deg1, p2, deg2, aspect)?.let { (baseOrb, _) ->
      val harmonicOrb = baseOrb / n
      val angle = deg1.getAngle(deg2)
      val angleDiff = abs(angle - aspect.degree)
      angleDiff <= harmonicOrb
    } ?: false
  }
}
