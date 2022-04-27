/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core.astrology

import destiny.core.IPattern

interface IAstroPattern : IPattern

data class PointSignPattern(val astroPoint: AstroPoint,
                            val sign: ZodiacSign) : IAstroPattern

data class PointHousePattern(val astroPoint: AstroPoint,
                             val house : Int) : IAstroPattern
