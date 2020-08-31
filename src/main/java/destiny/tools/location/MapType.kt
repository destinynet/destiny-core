package destiny.tools.location

import destiny.tools.ILocaleString
import java.util.*

fun MapType.asLocaleString() = object : ILocaleString {
  private val resource = "destiny.tools.location.MapType"
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.name)
  }
}

fun MapType.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

enum class MapType {
  roadmap, satellite, hybrid, terrain
}
