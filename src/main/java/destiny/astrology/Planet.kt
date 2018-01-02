/**
 * Created by smallufo on 2017-12-27.
 */
package destiny.astrology

import destiny.astrology.Planet.*
import java.util.*

object Planets {
  val classicalArray = arrayOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
  val array = arrayOf(*classicalArray, URANUS, NEPTUNE, PLUTO)
  val list = listOf(*array)
}

sealed class Planet(val nameKey: String,
                    val abbrKey: String) : Star(nameKey, abbrKey, Star::class.java.name) , Comparable<Planet> {

  object SUN     : Planet("Planet.SUN"    , "Planet.SUN_ABBR")
  object MOON    : Planet("Planet.MOON"   , "Planet.MOON_ABBR")
  object MERCURY : Planet("Planet.MERCURY", "Planet.MERCURY_ABBR")
  object VENUS   : Planet("Planet.VENUS"  , "Planet.VENUS_ABBR")
  object MARS    : Planet("Planet.MARS"   , "Planet.MARS_ABBR")
  object JUPITER : Planet("Planet.JUPITER", "Planet.JUPITER_ABBR")
  object SATURN  : Planet("Planet.SATURN" , "Planet.SATURN_ABBR")
  object URANUS  : Planet("Planet.URANUS" , "Planet.URANUS_ABBR")
  object NEPTUNE : Planet("Planet.NEPTUNE", "Planet.NEPTUNE_ABBR")
  object PLUTO   : Planet("Planet.PLUTO"  , "Planet.PLUTO_ABBR")

  override fun compareTo(other: Planet): Int {
    if (this === other)
      return 0
    return Planets.array.indexOf(this) - Planets.array.indexOf(other)
  }

  companion object {

    fun fromString(value: String): Planet? {
      return Planets.array.firstOrNull {
        it.getName(Locale.ENGLISH).equals(value, ignoreCase = true)
      }
    }
  }
}