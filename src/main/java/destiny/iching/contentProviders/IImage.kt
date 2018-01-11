/** 2009/7/13 上午 4:28:41 by smallufo  */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/** 卦 或 爻 的象曰  */
interface IImage {

  /** 取得卦的象曰  */
  fun getHexagramImage(hexagram: IHexagram, locale: Locale): String

  /** 取得爻的象曰 , lineIndex 亦可直接傳 0 或 7 進來  */
  fun getLineImage(hexagram: IHexagram, lineIndex: Int, locale: Locale): String

  /** 用九/用六的象曰  */
  fun getExtraImage(hexagram: IHexagram, locale: Locale): String
}

