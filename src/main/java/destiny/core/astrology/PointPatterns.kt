/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core.astrology

import destiny.core.IPattern

interface IAstroPattern : IPattern

interface IPointSignPattern : IAstroPattern {
  val point : AstroPoint
  val sign : ZodiacSign
}

data class PointSignPattern(override val point: AstroPoint,
                            override val sign: ZodiacSign) : IPointSignPattern

data class PointHousePattern(val astroPoint: AstroPoint,
                             val house : Int) : IAstroPattern
