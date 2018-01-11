/**
 * @author smallufo
 * Created on 2010/10/25 at 下午9:23:08
 */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/**
 * 卦 或 爻 的辭
 */
interface IExpression {

  /** 取得卦辭  */
  fun getHexagramExpression(hexagram: IHexagram, locale: Locale): String

  /**
   * @param hexagram
   * @param lineIndex 0~7 (0 的話則為針對卦辭)
   * @param locale
   * @return 取得爻辭
   */
  fun getLineExpression(hexagram: IHexagram, lineIndex: Int, locale: Locale): String

  /** 取得 用六/用九 的爻辭  */
  fun getExtraExpression(hexagram: IHexagram, locale: Locale): String
}
