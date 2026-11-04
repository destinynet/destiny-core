package destiny.core.fengshui

import destiny.tools.ILocaleString
import destiny.tools.I18nBundles
import destiny.tools.Lang

enum class NorthType : ILocaleString {
  /** 正北 */
  TRUE,

  /** 磁北 */
  MAGNETIC;

  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(NorthType::class.qualifiedName!!, lang, name) ?: name
  }
}
