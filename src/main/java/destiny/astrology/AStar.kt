/**
 * Created by smallufo on 2017-12-28.
 */
package destiny.astrology

import java.util.*

sealed class AStar(val nameKey: String,
                   val abbrKey: String) : Star(nameKey, abbrKey, Star::class.java.name) , Comparable<AStar> {

  object SUN  : AStar("Planet.SUN", "Planet.SUN_ABBR")
  object MOON : AStar("Planet.MOON", "Planet.MOON_ABBR")

  override fun compareTo(other: AStar): Int {
    if (this == other)
      return 0
    return values.indexOf(this) - values.indexOf(other)
  }

  companion object {

    val classicalValues = arrayOf(MOON, SUN)
    val values = arrayOf(SUN , MOON)

    fun fromString(value: String) : AStar? {
      return values.firstOrNull {
        it.getName(Locale.ENGLISH).equals(value, ignoreCase = true)
      }
    }
  }
}