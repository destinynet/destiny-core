/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core.astrology

import destiny.core.IPattern
import destiny.core.toString
import java.util.*

interface IAstroPattern : IPattern

interface IPointSignPattern : IAstroPattern {
  val point: AstroPoint
  val sign: ZodiacSign
}

data class PointSignPattern(override val point: AstroPoint,
                            override val sign: ZodiacSign) : IPointSignPattern {
  override fun getName(locale: Locale): String {
    return buildString {
      append(point.toString(locale))
      append(" 在 ")
      append(sign)
      append("座")
    }
  }
                            }

interface IPointHousePattern : IAstroPattern {
  val point: AstroPoint
  val house: Int
}

data class PointHousePattern(override val point: AstroPoint,
                             override val house : Int) : IPointHousePattern {
  override fun getName(locale: Locale): String {
    return buildString {
      append(point.toString(locale))
      append(" 在 第")
      append(house)
      append("宮")
    }
  }
                             }
