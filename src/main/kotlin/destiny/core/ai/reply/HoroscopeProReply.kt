/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.core.ai.reply

import destiny.core.astrology.AstroPattern
import destiny.core.astrology.IMidPointWithFocal
import destiny.core.astrology.IPointAspectPattern
import destiny.core.ai.Advice
import kotlinx.serialization.Serializable


class HoroscopeProReply {

  @Serializable
  data class AspectAnalysis(
    val pattern: IPointAspectPattern,
    val advice: Advice
  )

  @Serializable
  data class PatternAnalysis(
    val pattern: AstroPattern,
    val advice: Advice
  )

  @Serializable
  data class MidpointAnalysis(
    val midpoint: IMidPointWithFocal,
    val advice: Advice
  )

}
