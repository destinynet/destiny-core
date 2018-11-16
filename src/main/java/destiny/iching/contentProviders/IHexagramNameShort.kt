/** 2009/7/13 上午 3:35:07 by smallufo  */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/** 取得「短」的卦名，例如「乾」  */
interface IHexagramNameShort {

  /** 取得「短」的卦名，例如「乾」  */
  @JvmDefault
  fun getNameShort(hexagram: IHexagram, locale: Locale): String

  /** 從卦的「短卦名」，反查回 Hexagram  */
  @JvmDefault
  fun getHexagram(name: String, locale: Locale): IHexagram

}

