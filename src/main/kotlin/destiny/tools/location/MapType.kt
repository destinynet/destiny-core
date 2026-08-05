package destiny.tools.location

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang

fun MapType.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.tools.location.MapType"
  override fun getTitle(locale: Locale): String {
    return I18nBundles.string(resource, locale.toLang(), this@asLocaleString.name) ?: this@asLocaleString.name
  }
}

fun MapType.toString(locale: Locale): String {
  return this.asLocaleString().getTitle(locale)
}

enum class MapType {
  roadmap, satellite, hybrid, terrain
}
