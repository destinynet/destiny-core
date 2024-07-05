/**
 * Created by smallufo on 2024-07-05.
 */
package destiny.core.astrology

import destiny.core.Gender
import java.io.Serializable


enum class DiceStar(val star: Star, val unicode: Char = star.unicode!!) {
  SUN(Planet.SUN),
  MOON(Planet.MOON),
  MERCURY(Planet.MERCURY),
  VENUS(Planet.VENUS),
  MARS(Planet.MARS),
  JUPITER(Planet.JUPITER),
  SATURN(Planet.SATURN),
  URANUS(Planet.URANUS),
  NEPTUNE(Planet.NEPTUNE),
  PLUTO(Planet.PLUTO),
  NORTH(LunarNode.NORTH_MEAN),
  SOUTH(LunarNode.SOUTH_MEAN),
}

interface IDiceModel : Serializable {
  val star: DiceStar
  val signal: ZodiacSign
  val house: Int
  val gender: Gender?
  val question: String?
}

@kotlinx.serialization.Serializable
data class DiceModel(override val star: DiceStar,
                     override val signal: ZodiacSign,
                     override val house: Int,
                     override val gender: Gender?,
                     override val question: String?) : IDiceModel {
  companion object {
    val random : IDiceModel
      get() {
        val star = DiceStar.entries.random()
        val signal = ZodiacSign.entries.random()
        val house = (1..12).random()
        return DiceModel(star, signal, house, null, null)
      }
  }
}
