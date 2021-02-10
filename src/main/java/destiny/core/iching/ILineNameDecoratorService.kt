/**
 * @author smallufo
 * Created on 2010/11/3 at 上午10:54:47
 */
package destiny.core.iching

import java.util.*


interface ILineNameDecorator {

  /** 取得支援的 locale  */
  val supportedLocale: Locale

  /**
   * 將 1~6 , 以及陰陽 , 翻譯成 初九(六) 到 上九(六)
   * @param lineIndex 1~6 for 乾坤之外 , 1~7 for 乾坤
   */
  fun getName(hexagram: IHexagram, lineIndex: Int): String
}


interface ILineNameDecoratorService {

  /**
   * 將 1~6 , 以及陰陽 , 翻譯成 初九(六) 到 上九(六)
   *
   * @param lineIndex 1~6 (乾坤之外) , 1~7 for 乾坤
   */
  fun getName(hexagram: IHexagram, lineIndex: Int, locale: Locale = Locale.getDefault()): String
}
