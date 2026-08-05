package destiny.core.fengshui

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang
import destiny.tools.Lang

fun NorthType.asLocaleString() = object : ILocaleString {
  private val resource = NorthType::class.qualifiedName!!
  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(resource, lang, this@asLocaleString.name) ?: this@asLocaleString.name
  }
}

fun NorthType.toString(lang: Lang): String {
  return this.asLocaleString().getTitle(lang)
}

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun NorthType.toString(locale: Locale): String = toString(locale.toLang())

enum class NorthType {
  /** 正北 */
  TRUE,

  /** 磁北 */
  MAGNETIC;
}
