/** 2009/7/13 上午 4:03:01 by smallufo  */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/** 384爻辭 + 用九/用六 兩個 extra  */
interface ILineExpression {

  fun getLineExpression(hexagram: IHexagram, line: Int, locale: Locale): String

  fun getExtraExpression(hexagram: IHexagram, locale: Locale): String?
}

