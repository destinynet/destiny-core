package destiny.tools.location

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang
import destiny.tools.Lang

fun MapType.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.tools.location.MapType"
  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(resource, lang, this@asLocaleString.name) ?: this@asLocaleString.name
  }
}

fun MapType.toString(lang: Lang): String {
  return this.asLocaleString().getTitle(lang)
}

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun MapType.toString(locale: Locale): String = toString(locale.toLang())

enum class MapType {
  roadmap, satellite, hybrid, terrain
}
