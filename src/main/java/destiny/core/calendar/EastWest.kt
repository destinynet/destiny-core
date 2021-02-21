/**
 * Created by smallufo on 2018-03-15.
 */
package destiny.core.calendar

import destiny.tools.ILocaleString
import java.util.*

fun EastWest.asLocaleString() = object : ILocaleString {
  val resource = EastWest::class.qualifiedName!!
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun EastWest.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}

enum class EastWest(val nameKey: String) {
  EAST("Location.EAST"), WEST("Location.WEST");

  companion object {
    fun of(c: Char): EastWest {
      if (c == 'E' || c == 'e')
        return EAST
      if (c == 'W' || c == 'w')
        return WEST
      throw IllegalArgumentException("char '$c' only accepts 'E' , 'e' , 'W' , or 'w'. ")
    }
  }
}
