/** 2009/7/13 上午 3:58:05 by smallufo  */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/** 卦辭  */
interface IHexagramExpression {

  fun getHexagramExpression(hexagram: IHexagram, locale: Locale): String
}

