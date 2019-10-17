/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.astrology.classical.rules

import destiny.astrology.*
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.MutualDataWithSign
import destiny.core.DayNight
import destiny.core.chinese.YinYang

/**
 * 行星的 25種狀態
 * https://site.douban.com/183595/widget/notes/192509582/note/600376742/
 */

sealed class EssentialDignity(override val name: String,
                              override val notes: String? = null) : IPlanetPattern {

  override val type: RuleType = RuleType.ESSENTIAL

  data class Ruler(override val planet: Planet, val sign: ZodiacSign) : EssentialDignity(Ruler::class.java.simpleName)
  data class Exaltation(override val planet: Planet, val sign: ZodiacSign) : EssentialDignity(Exaltation::class.java.simpleName)
  data class Triplicity(override val planet: Planet, val sign: ZodiacSign, val dayNight: DayNight) : EssentialDignity(Triplicity::class.java.simpleName)
  data class Term(override val planet: Planet, val lngDeg: Double) : EssentialDignity(Term::class.java.simpleName)
  data class Face(override val planet: Planet, val lngDeg: Double) : EssentialDignity(Face::class.java.simpleName)

  data class MutualReception(override val planet: Planet, val sign1: ZodiacSign, val dig1: Dignity,
                             val p2: Point, val sign2: ZodiacSign, val dig2: Dignity)
    : EssentialDignity(MutualReception::class.java.simpleName) {
    private val mutualData = MutualDataWithSign(planet, sign1, dig1, p2, sign2, dig2)
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is MutualReception) return false

      if (mutualData != other.mutualData) return false

      return true
    }

    override fun hashCode(): Int {
      return mutualData.hashCode()
    }

  }
}

sealed class AccidentalDignity(override val name: String,
                               override val notes: String? = null) : IPlanetPattern {

  override val type: RuleType = RuleType.ACCIDENTAL

  data class House_1_10(override val planet: Planet, val house: Int) : AccidentalDignity(House_1_10::class.java.simpleName)
  data class House_4_7_11(override val planet: Planet, val house: Int) : AccidentalDignity(House_4_7_11::class.java.simpleName)
  data class House_2_5(override val planet: Planet, val house: Int) : AccidentalDignity(House_2_5::class.java.simpleName)
  data class House_9(override val planet: Planet) : AccidentalDignity(House_9::class.java.simpleName)
  data class House_3(override val planet: Planet) : AccidentalDignity(House_3::class.java.simpleName)
  data class Direct(override val planet: Planet) : AccidentalDignity(Direct::class.java.simpleName)
  data class Swift(override val planet: Planet) : AccidentalDignity(Swift::class.java.simpleName)
  data class Oriental(override val planet: Planet) : AccidentalDignity(Oriental::class.java.simpleName)
  data class Occidental(override val planet: Planet) : AccidentalDignity(Occidental::class.java.simpleName)
  object Moon_Increase_Light : AccidentalDignity(Moon_Increase_Light::class.java.simpleName) {
    override val planet: Planet = Planet.MOON
  }

  data class Free_Combustion(override val planet: Planet) : AccidentalDignity(Free_Combustion::class.java.simpleName)
  data class Cazimi(override val planet: Planet) : AccidentalDignity(Cazimi::class.java.simpleName)
  data class Partile_Conj_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity(Partile_Conj_Jupiter_Venus::class.java.simpleName)
  data class Partile_Conj_North_Node(override val planet: Planet , val node: LunarNode) : AccidentalDignity(Partile_Conj_North_Node::class.java.simpleName)
  data class Partile_Trine_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity(Partile_Trine_Jupiter_Venus::class.java.simpleName)
  data class Partile_Sextile_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity(Partile_Sextile_Jupiter_Venus::class.java.simpleName)
  data class Partile_Conj_Regulus(override val planet: Planet) : AccidentalDignity(Partile_Conj_Regulus::class.java.simpleName)
  data class Partile_Conj_Spica(override val planet: Planet) : AccidentalDignity(Partile_Conj_Spica::class.java.simpleName)
  data class JoyHouse(override val planet: Planet, val house: Int) : AccidentalDignity(JoyHouse::class.java.simpleName)
  data class Hayz(override val planet: Planet, val dayNight: DayNight, val yinYang: YinYang, val sign: ZodiacSign) : AccidentalDignity(Hayz::class.java.simpleName)
  data class Besieged_Jupiter_Venus(override val planet: Planet) : AccidentalDignity(Besieged_Jupiter_Venus::class.java.simpleName)
  data class Translation_of_Light(override val planet: Planet, val from: Planet, val to: Planet, val deg: Double, val aspect: AspectData.Type?) : AccidentalDignity(Translation_of_Light::class.java.simpleName)
  data class Collection_of_Light(override val planet: Planet, val twoPlanets: List<Planet>, val angle: Double) : AccidentalDignity(Collection_of_Light::class.java.simpleName)
  data class Refrain_from_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet, val aspect: Aspect) : AccidentalDignity(Refrain_from_Mars_Saturn::class.java.simpleName)
}


sealed class Debility(override val name: String,
                      override val notes: String? = null) : IPlanetPattern {
  override val type: RuleType = RuleType.DEBILITY

  data class Detriment(override val planet: Planet , val sign: ZodiacSign) : Debility(Detriment::class.java.simpleName)
  data class Fall(override val planet: Planet , val sign: ZodiacSign) : Debility(Fall::class.java.simpleName)
  data class Peregrine(override val planet: Planet) : Debility(Peregrine::class.java.simpleName)
  data class House_12(override val planet: Planet) : Debility(House_12::class.java.simpleName)
  data class House_6_8(override val planet: Planet, val house: Int) : Debility(House_6_8::class.java.simpleName)
  data class Retrograde(override val planet: Planet) : Debility(Retrograde::class.java.simpleName)
  data class Slower(override val planet: Planet) : Debility(Slower::class.java.simpleName)
  data class Occidental(override val planet: Planet) : Debility(Occidental::class.java.simpleName)
  data class Oriental(override val planet: Planet) : Debility(Oriental::class.java.simpleName)
  object Moon_Decrease_Light : Debility(Moon_Decrease_Light::class.java.simpleName) {
    override val planet: Planet = Planet.MOON
  }

  data class Combustion(override val planet: Planet) : Debility(Combustion::class.java.simpleName)
  data class Sunbeam(override val planet: Planet) : Debility(Sunbeam::class.java.simpleName)
  data class Partile_Conj_Mars_Saturn(override val planet: Planet , val marsOrSaturn: Planet) : Debility(Partile_Conj_Mars_Saturn::class.java.simpleName)
  data class Partile_Conj_South_Node(override val planet: Planet) : Debility(Partile_Conj_South_Node::class.java.simpleName)
  data class Besieged_Mars_Saturn(override val planet: Planet) : Debility(Besieged_Mars_Saturn::class.java.simpleName)
  data class Partile_Oppo_Mars_Saturn(override val planet: Planet , val marsOrSaturn: Planet) : Debility(Partile_Oppo_Mars_Saturn::class.java.simpleName)
  data class Partile_Square_Mars_Saturn(override val planet: Planet , val marsOrSaturn: Planet) : Debility(Partile_Square_Mars_Saturn::class.java.simpleName)
  data class Conj_Algol(override val planet: Planet) : Debility(Conj_Algol::class.java.simpleName)
  data class Out_of_Sect(override val planet: Planet , val dayNight: DayNight , val yinYang: YinYang , val sign: ZodiacSign) : Debility(Out_of_Sect::class.java.simpleName)
  data class Refrain_from_Venus_Jupiter(override val planet: Planet , val venusOrJupiter: Planet , val aspect: Aspect) : Debility(Refrain_from_Venus_Jupiter::class.java.simpleName)

  data class MutualDeception(override val planet: Planet, val sign1: ZodiacSign, val dig1: Dignity,
                             val p2: Point, val sign2: ZodiacSign, val dig2: Dignity) : Debility(MutualDeception::class.java.simpleName) {
    private val mutualData = MutualDataWithSign(planet, sign1, dig1, p2, sign2, dig2)
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is MutualDeception) return false

      if (mutualData != other.mutualData) return false

      return true
    }

    override fun hashCode(): Int {
      return mutualData.hashCode()
    }

  }
}
