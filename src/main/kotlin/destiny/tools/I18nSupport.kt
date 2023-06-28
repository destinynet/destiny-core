/**
 * @author smallufo
 * Created on 2010/10/10 at 下午9:32:54
 */
package destiny.tools

import java.util.*

interface I18nSupport {

  /** 取得資料庫中(或其他來源) 支援的 Locales  */
  val supportedLocales: Iterable<Locale>
}
