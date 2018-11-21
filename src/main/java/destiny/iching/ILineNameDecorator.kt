/**
 * @author smallufo
 * Created on 2010/11/3 at 上午10:54:47
 */
package destiny.iching

import java.util.*

interface ILineNameDecorator {

  /**
   * 將 1~6 , 以及陰陽 , 翻譯成 初九(六) 到 上九(六)
   *
   * @param lineIndex 1~6 (乾坤之外) , 1~7 for 乾坤
   */
  fun getLineName(locale: Locale, hexagram: IHexagram, lineIndex: Int): String
}
