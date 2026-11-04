package destiny.tools.location

import destiny.tools.ILocaleString
import destiny.tools.I18nBundles
import destiny.tools.Lang

enum class MapType : ILocaleString {
  roadmap, satellite, hybrid, terrain;

  override fun getTitle(lang: Lang): String {
    return I18nBundles.string("destiny.tools.location.MapType", lang, name) ?: name
  }
}
