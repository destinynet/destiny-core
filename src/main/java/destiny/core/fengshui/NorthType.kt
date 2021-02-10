package destiny.core.fengshui

import destiny.tools.ILocaleString
import java.util.*

fun NorthType.asLocaleString() = object : ILocaleString {
  private val resource = NorthType::class.qualifiedName!!
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.name)
  }
}

fun NorthType.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

enum class NorthType {
  /** 正北 */
  TRUE,

  /** 磁北 */
  MAGNETIC;
}
