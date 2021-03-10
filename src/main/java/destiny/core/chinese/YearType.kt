package destiny.core.chinese

import destiny.tools.ILocaleString
import java.util.*

/** 「年」的分界  */
enum class YearType {
  YEAR_LUNAR, // 初一為界
  YEAR_SOLAR; // 立春為界
}

fun YearType.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

fun YearType.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(YearType::class.java.name, locale).getString(name)
  }
}
