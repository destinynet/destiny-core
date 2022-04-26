/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core.astrology

import destiny.core.IPattern


interface IPointSignPattern : IPattern {
  val astroPoint: AstroPoint
  val sign: ZodiacSign
}

data class PointSignPattern(override val astroPoint: AstroPoint,
                            override val sign: ZodiacSign) : IPointSignPattern
