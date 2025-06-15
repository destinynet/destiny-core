/**
 * Created by smallufo on 2022-08-04.
 */
package destiny.core.astrology

import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import kotlin.math.abs


/**
 * 不論星體、不論交角，只要 error 在 [orb] 範圍之內，就算有效
 */
class AspectEffectiveFixedOrb(val orb: Double) : IAspectEffective {

  override val applicableAspects: Set<Aspect> = Aspect.entries.toSet()

  override fun getEffectiveErrorAndScore(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Pair<Double, Score>? {

    val angle = deg1.getAngle(deg2)
    val angleDiff = abs(angle - aspect.degree)

    val threshold = 0.6

    return angleDiff
      .takeIf { it <= orb }
      ?.let { it to (threshold + (1 - threshold) * (orb - angleDiff) / orb).toScore() }
  }

  override fun isEffective(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Boolean {
    val angle = deg1.getAngle(deg2)
    val angleDiff = abs(angle - aspect.degree)
    return angleDiff <= orb
  }
}
