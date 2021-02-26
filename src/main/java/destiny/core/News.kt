/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core

import java.io.Serializable
import java.util.*


sealed class News : Serializable {

  sealed class NorthSouth : News() {
    object NORTH : NorthSouth()
    object SOUTH : NorthSouth()
    companion object {
      fun of(c: Char): NorthSouth? {
        if (c == 'N' || c == 'n')
          return NORTH
        if (c == 'S' || c == 's')
          return SOUTH
        return null
      }
    }
  }

  sealed class EastWest : News() {
    object EAST : EastWest()
    object WEST : EastWest()

    companion object {
      fun of(c: Char): EastWest? {
        if (c == 'E' || c == 'e')
          return EAST
        if (c == 'W' || c == 'w')
          return WEST
        return null
      }
    }
  }

  override fun toString(): String {
    return this::class.simpleName!!
  }

  companion object {
    fun of(c: Char): News? {
      return NorthSouth.of(c) ?: EastWest.of(c)
    }
  }
}


/**
 * 北緯 / 南緯
 */
fun News.NorthSouth.toCoordinateString(locale: Locale): String {
  val nameKey = when (this) {
    is News.NorthSouth.NORTH -> "NORTH.Coordinate"
    is News.NorthSouth.SOUTH -> "SOUTH.Coordinate"
  }
  val resource = News::class.qualifiedName!!
  return ResourceBundle.getBundle(resource, locale).getString(nameKey)
}


/**
 * 東經 / 西經
 */
fun News.EastWest.toCoordinateString(locale: Locale): String {
  val nameKey = when (this) {
    is News.EastWest.EAST -> "EAST.Coordinate"
    is News.EastWest.WEST -> "WEST.Coordinate"
  }
  val resource = News::class.qualifiedName!!
  return ResourceBundle.getBundle(resource, locale).getString(nameKey)
}

/** 東 / 西 / 南 / 北 */
fun News.toString(locale: Locale): String {
  val nameKey = this::class.simpleName!!
  val resource = News::class.qualifiedName!!
  return ResourceBundle.getBundle(resource, locale).getString(nameKey)
}
