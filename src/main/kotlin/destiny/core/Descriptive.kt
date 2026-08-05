/**
 * @author smallufo
 * Created on 2006/5/5 at 上午 04:20:08
 */
package destiny.core

import destiny.tools.ILocaleString
import destiny.tools.Lang
import destiny.tools.toLang
import java.util.*

interface Descriptive : ILocaleString {

  /** 詳細描述  */
  fun getDescription(lang: Lang): String {
    return getTitle(lang)
  }

  /** 橋接，說明見 [ILocaleString]。**實作端不要覆寫。** */
  fun getDescription(locale: Locale): String = getDescription(locale.toLang())
}
