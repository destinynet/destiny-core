/**
 * Created by smallufo on 2017-12-27.
 */
package destiny.core.astrology

import java.util.*

sealed class Planet(nameKey: String,
                    abbrKey: String,
                    unicode: Char) : Star(nameKey, abbrKey, Star::class.java.name , unicode) , Comparable<Planet> {

  object SUN     : Planet("Planet.SUN"    , "Planet.SUN_ABBR" , '☉')
  object MOON    : Planet("Planet.MOON", "Planet.MOON_ABBR", '☽')
  object MERCURY : Planet("Planet.MERCURY", "Planet.MERCURY_ABBR", '☿')
  object VENUS   : Planet("Planet.VENUS", "Planet.VENUS_ABBR", '♀')
  object MARS    : Planet("Planet.MARS", "Planet.MARS_ABBR", '♂')
  object JUPITER : Planet("Planet.JUPITER", "Planet.JUPITER_ABBR", '♃')
  object SATURN  : Planet("Planet.SATURN", "Planet.SATURN_ABBR", '♄')
  object URANUS  : Planet("Planet.URANUS", "Planet.URANUS_ABBR", '♅')
  object NEPTUNE : Planet("Planet.NEPTUNE", "Planet.NEPTUNE_ABBR", '♆')
  object PLUTO   : Planet("Planet.PLUTO", "Planet.PLUTO_ABBR", '♇')

  override fun compareTo(other: Planet): Int {
    if (this === other)
      return 0
    return array.indexOf(this) - array.indexOf(other)
  }

  companion object {

    val classicalArray by lazy { arrayOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN) }
    val classicalList by lazy { listOf(*classicalArray) }

    val array by lazy { arrayOf(*classicalArray, URANUS, NEPTUNE, PLUTO) }
    val list by lazy { listOf(*array) }

    fun fromString(value: String): Planet? {
      return array.firstOrNull {
        it.toString(Locale.ENGLISH).equals(value, ignoreCase = true)
      }
    }
  }
}
