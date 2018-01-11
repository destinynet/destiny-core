/** 2009/7/13 上午 4:00:28 by smallufo  */
package destiny.iching.contentProviders

import destiny.iching.IHexagram

import java.util.Locale

/** 彖曰  */
interface IHexagramJudgement {

  fun getJudgement(hexagram: IHexagram, locale: Locale): String
}

