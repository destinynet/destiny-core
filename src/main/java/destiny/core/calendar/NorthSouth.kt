/**
 * Created by smallufo on 2018-03-15.
 */
package destiny.core.calendar

import java.util.*

enum class NorthSouth(private val nameKey: String) {
  NORTH("Location.NORTH"), SOUTH("Location.SOUTH");

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  companion object {

    private val resource = Location::class.java.name

    fun getNorthSouth(c: Char): NorthSouth {
      if (c == 'N' || c == 'n')
        return NORTH
      if (c == 'S' || c == 's')
        return SOUTH
      throw IllegalArgumentException("char '$c' only accepts 'N' , 'n' , 'S' , or 's'. ")
    }
  }
}
