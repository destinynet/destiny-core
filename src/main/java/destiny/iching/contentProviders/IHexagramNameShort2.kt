/**
 * Created by smallufo on 2018-11-17.
 */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

interface IHexagramNameShort2 {

  /** 取得「短」的卦名，例如「乾」  */
  fun getNameShort(hexagram: IHexagram, locale: Locale): String

  /** 從卦的「短卦名」，反查回 Hexagram  */
  fun getHexagram(name: String, locale: Locale): IHexagram
}