/**
 * @author smallufo
 * Created on 2006/5/5 at 上午 04:20:08
 */
package destiny.core

import destiny.tools.ILocaleString
import destiny.tools.Lang

/**
 * `Locale` 版的 `getDescription(locale)` 是 JVM 橋接擴充函式（`destiny.core` 的 `LocaleBridges.kt`），
 * 呼叫端要 `import destiny.core.getDescription`。說明見 [ILocaleString]。
 */
interface Descriptive : ILocaleString {

  /** 詳細描述  */
  fun getDescription(lang: Lang): String {
    return getTitle(lang)
  }
}
