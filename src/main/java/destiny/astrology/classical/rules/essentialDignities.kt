/**
 * Created by kevin.huang on 2019-09-26.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.astrology.ZodiacSign

abstract class EssentialDignity(override val name: String,
                                override val notes: String? = null) : IClassicalPattern {
  override val type: RuleType = RuleType.ESSENTIAL
}

/** A planet in its own sign , or mutual reception with another planet by sign  */

sealed class Ruler(override val planet: Planet) : EssentialDignity(Ruler::class.java.simpleName) {
  abstract val sign : ZodiacSign

  data class Basic(override val planet: Planet,
                   override val sign: ZodiacSign,
                   override val notes: String?) : Ruler(planet)

  data class MutialReception(override val planet: Planet,
                             override val sign: ZodiacSign,
                             val planet2: Planet,
                             val sign2: ZodiacSign,
                             override val notes: String?) : Ruler(planet)
}


/** A planet in its exaltation , or mutual reception with another planet by exaltation  */
sealed class Exaltation(override val planet: Planet,
                        override val notes: String? = null) : EssentialDignity(Exaltation::class.java.simpleName) {
  abstract val sign: ZodiacSign

  data class Basic(override val planet: Planet,
                   override val sign: ZodiacSign) : Exaltation(planet)

  data class MutualReception(override val planet: Planet,
                             override val sign: ZodiacSign) : Exaltation(planet)
}

/** A planet in itw own term.  */
data class Term(override val planet: Planet,
                val lngDeg: Double,
                override val notes: String? = null) : EssentialDignity(Term::class.java.simpleName) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Term) return false

    if (planet != other.planet) return false

    return true
  }

  override fun hashCode(): Int {
    return planet.hashCode()
  }
}

/** A planet in its own Chaldean decanate or face.  */
data class Face(override val planet: Planet,
                val lngDeg: Double,
                override val notes: String? = null) : EssentialDignity(Face::class.java.simpleName) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Face) return false

    if (planet != other.planet) return false

    return true
  }

  override fun hashCode(): Int {
    return planet.hashCode()
  }
}
