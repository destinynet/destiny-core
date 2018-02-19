/** 2009/7/13 上午 4:00:28 by smallufo  */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/** 彖曰 , 只有卦，才有彖曰 */
interface IHexagramJudgement {

  fun getJudgement(hexagram: IHexagram, locale: Locale): String
}

