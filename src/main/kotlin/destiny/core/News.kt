/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core

import java.io.Serializable
import java.util.*


sealed class News : Serializable {

  sealed class NorthSouth : News() {
    data object NORTH : NorthSouth() {
      private fun readResolve(): Any = NORTH
    }

    data object SOUTH : NorthSouth() {
      private fun readResolve(): Any = SOUTH
    }

    companion object {
      fun of(c: Char): NorthSouth? {
        return when (c.uppercaseChar()) {
          'N'  -> NORTH
          'S'  -> SOUTH
          else -> null
        }
      }
    }
  }

  sealed class EastWest : News() {
    data object EAST : EastWest() {
      private fun readResolve(): Any = EAST
    }

    data object WEST : EastWest() {
      private fun readResolve(): Any = WEST
    }

    companion object {
      fun of(c: Char): EastWest? {
        return when (c.uppercaseChar()) {
          'E'  -> EAST
          'W'  -> WEST
          else -> null
        }
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
fun News.getTitle(locale: Locale): String {
  val nameKey = this::class.simpleName!!
  val resource = News::class.qualifiedName!!
  return ResourceBundle.getBundle(resource, locale).getString(nameKey)
}
