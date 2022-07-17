/**
 * @author smallufo
 * Created on 2006/5/5 at 上午 04:20:08
 */
package destiny.core

import destiny.tools.ILocaleString
import java.util.*

interface Descriptive : ILocaleString {

  /** 詳細描述  */
  fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}
