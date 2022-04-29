/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core.astrology

import destiny.core.IPattern

interface IAstroPattern : IPattern

interface IPointSignPattern : IAstroPattern {
  val point: AstroPoint
  val sign: ZodiacSign
}

data class PointSignPattern(override val point: AstroPoint,
                            override val sign: ZodiacSign) : IPointSignPattern

interface IPointHousePattern : IAstroPattern {
  val point: AstroPoint
  val house: Int
}

data class PointHousePattern(override val point: AstroPoint,
                             override val house : Int) : IPointHousePattern
