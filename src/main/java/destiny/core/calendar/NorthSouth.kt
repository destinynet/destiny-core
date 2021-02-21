/**
 * Created by smallufo on 2018-03-15.
 */
package destiny.core.calendar

import destiny.tools.ILocaleString
import java.util.*

fun NorthSouth.asLocaleString() = object : ILocaleString {
  val resource = NorthSouth::class.qualifiedName!!
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun NorthSouth.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

enum class NorthSouth(val nameKey: String) {
  NORTH("Location.NORTH"), SOUTH("Location.SOUTH");

  companion object {
    fun of(c: Char): NorthSouth {
      if (c == 'N' || c == 'n')
        return NORTH
      if (c == 'S' || c == 's')
        return SOUTH
      throw IllegalArgumentException("char '$c' only accepts 'N' , 'n' , 'S' , or 's'. ")
    }
  }
}
