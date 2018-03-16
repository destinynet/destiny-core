/**
 * Created by smallufo on 2018-03-15.
 */
package destiny.core.calendar

import java.util.*

enum class EastWest(private val nameKey: String) {
  EAST("Location.EAST"), WEST("Location.WEST");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private val resource = EastWest::class.java.name

    fun getEastWest(c: Char): EastWest {
      if (c == 'E' || c == 'e')
        return EAST
      if (c == 'W' || c == 'w')
        return WEST
      throw IllegalArgumentException("char '$c' only accepts 'E' , 'e' , 'W' , or 'w'. ")
    }
  }
}
