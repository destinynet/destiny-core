/** 2009/6/18 by smallufo  */
package destiny.iching

import destiny.core.Descriptive
import java.util.*


/** 對一個「卦」的註釋  */
interface HexagramContentProvider : Descriptive {

  /** 支援哪種(些)語言  */
  val locales: Collection<Locale>

  /** 取得此卦的解釋 , 如果沒有，則傳回 null  */
  fun getValue(hexagram: IHexagram, locale: Locale): String

  /**
   * @param lineIndex 1<=lineIndex<=6
   * @return 取得第幾爻的解釋 , 如果沒有，則傳回 null
   */
  fun getLineValue(hexagram: IHexagram, lineIndex: Int, locale: Locale): String

  /** 額外的註解（ex : 用九、用六） , 如果沒有，則傳回 null  */
  fun getExtraExpression(hexagram: IHexagram, locale: Locale): String

}
