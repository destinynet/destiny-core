/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core

import java.util.*


sealed class News {

  sealed class NorthSouth : News() {
    object NORTH : NorthSouth()
    object SOUTH : NorthSouth()
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

  sealed class EastWest : News() {
    object EAST : EastWest()
    object WEST : EastWest()

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

  override fun toString(): String {
    return this::class.simpleName!!
  }
}

fun News.NorthSouth.toCoordinateString(locale: Locale): String {
  val nameKey = when (this) {
    is News.NorthSouth.NORTH -> "NORTH.Coordinate"
    is News.NorthSouth.SOUTH -> "SOUTH.Coordinate"
  }
  val resource = News::class.simpleName!!
  return ResourceBundle.getBundle(resource, locale).getString(nameKey)
}

fun News.EastWest.toCoordinateString(locale: Locale): String {
  val nameKey = when (this) {
    is News.EastWest.EAST -> "EAST.Coordinate"
    is News.EastWest.WEST -> "WEST.Coordinate"
  }
  val resource = News::class.simpleName!!
  return ResourceBundle.getBundle(resource, locale).getString(nameKey)
}
