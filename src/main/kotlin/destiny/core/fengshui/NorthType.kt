package destiny.core.fengshui

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang

fun NorthType.asLocaleString() = object : ILocaleString {
  private val resource = NorthType::class.qualifiedName!!
  override fun getTitle(locale: Locale): String {
    return I18nBundles.string(resource, locale.toLang(), this@asLocaleString.name) ?: this@asLocaleString.name
  }
}

fun NorthType.toString(locale: Locale): String {
  return this.asLocaleString().getTitle(locale)
}

enum class NorthType {
  /** 正北 */
  TRUE,

  /** 磁北 */
  MAGNETIC;
}
