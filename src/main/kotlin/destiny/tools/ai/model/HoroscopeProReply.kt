/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.tools.ai.model

import destiny.core.astrology.AstroPattern
import destiny.core.astrology.IMidPointWithFocal
import destiny.core.astrology.IPointAspectPattern


class HoroscopeProReply {

  data class AspectAnalysis(
    val pattern: IPointAspectPattern,
    val advice: Advice
  )

  data class PatternAnalysis(
    val pattern: AstroPattern,
    val advice: Advice
  )

  data class MidpointAnalysis(
    val midpoint: IMidPointWithFocal,
    val advice: Advice
  )

}
