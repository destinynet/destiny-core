/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.core.astrology.classical.rules

import destiny.core.DayNight
import destiny.core.astrology.*
import destiny.core.astrology.classical.Dignity
import destiny.core.astrology.classical.MutualDataWithSign
import destiny.core.chinese.YinYang

/**
 * 行星的 25種狀態
 * https://site.douban.com/183595/widget/notes/192509582/note/600376742/
 */

sealed class EssentialDignity : IPlanetPattern {

  override val type: RuleType = RuleType.ESSENTIAL

  data class Ruler(override val planet: Planet, val sign: ZodiacSign) : EssentialDignity()
  data class Exaltation(override val planet: Planet, val sign: ZodiacSign) : EssentialDignity()
  data class Triplicity(override val planet: Planet, val sign: ZodiacSign, val dayNight: DayNight) : EssentialDignity()
  data class Term(override val planet: Planet, val lngDeg: Double) : EssentialDignity()
  data class Face(override val planet: Planet, val lngDeg: Double) : EssentialDignity()

  data class MutualReception(override val planet: Planet,
                             val sign1: ZodiacSign,
                             val dig1: Dignity,
                             val p2: Point,
                             val sign2: ZodiacSign,
                             val dig2: Dignity) : EssentialDignity() {
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

sealed class AccidentalDignity : IPlanetPattern {

  override val type: RuleType = RuleType.ACCIDENTAL

  data class House_1_10(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class House_4_7_11(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class House_2_5(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class House_9(override val planet: Planet) : AccidentalDignity()
  data class House_3(override val planet: Planet) : AccidentalDignity()
  data class Direct(override val planet: Planet) : AccidentalDignity()
  data class Swift(override val planet: Planet) : AccidentalDignity()
  data class Oriental(override val planet: Planet) : AccidentalDignity()
  data class Occidental(override val planet: Planet) : AccidentalDignity()
  object Moon_Increase_Light : AccidentalDignity() {
    override val planet: Planet = Planet.MOON
  }

  data class Free_Combustion(override val planet: Planet) : AccidentalDignity()
  data class Cazimi(override val planet: Planet) : AccidentalDignity()
  data class Partile_Conj_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity()
  data class Partile_Conj_North_Node(override val planet: Planet, val node: LunarNode) : AccidentalDignity()
  data class Partile_Trine_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity()
  data class Partile_Sextile_Jupiter_Venus(override val planet: Planet, val venusOrJupiter: Planet) : AccidentalDignity()
  data class Partile_Conj_Regulus(override val planet: Planet) : AccidentalDignity()
  data class Partile_Conj_Spica(override val planet: Planet) : AccidentalDignity()
  data class JoyHouse(override val planet: Planet, val house: Int) : AccidentalDignity()
  data class Hayz(override val planet: Planet, val dayNight: DayNight, val yinYang: YinYang, val sign: ZodiacSign) : AccidentalDignity()
  data class Besieged_Jupiter_Venus(override val planet: Planet) : AccidentalDignity()
  data class Translation_of_Light(override val planet: Planet, val from: Planet, val to: Planet, val deg: Double, val aspect: IAspectData.Type?) :
    AccidentalDignity()

  data class Collection_of_Light(override val planet: Planet, val twoPlanets: List<Planet>, val angle: Double) : AccidentalDignity()
  data class Refrain_from_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet, val aspect: Aspect) : AccidentalDignity()
}


sealed class Debility : IPlanetPattern {
  override val type: RuleType = RuleType.DEBILITY

  data class Detriment(override val planet: Planet, val sign: ZodiacSign) : Debility()
  data class Fall(override val planet: Planet, val sign: ZodiacSign) : Debility()
  data class Peregrine(override val planet: Planet) : Debility()
  data class House_12(override val planet: Planet) : Debility()
  data class House_6_8(override val planet: Planet, val house: Int) : Debility()
  data class Retrograde(override val planet: Planet) : Debility()
  data class Slower(override val planet: Planet) : Debility()
  data class Occidental(override val planet: Planet) : Debility()
  data class Oriental(override val planet: Planet) : Debility()
  object Moon_Decrease_Light : Debility() {
    override val planet: Planet = Planet.MOON
  }

  data class Combustion(override val planet: Planet) : Debility()
  data class Sunbeam(override val planet: Planet) : Debility()
  data class Partile_Conj_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet) : Debility()
  data class Partile_Conj_South_Node(override val planet: Planet) : Debility()
  data class Besieged_Mars_Saturn(override val planet: Planet) : Debility()
  data class Partile_Oppo_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet) : Debility()
  data class Partile_Square_Mars_Saturn(override val planet: Planet, val marsOrSaturn: Planet) : Debility()
  data class Conj_Algol(override val planet: Planet) : Debility()
  data class Out_of_Sect(override val planet: Planet, val dayNight: DayNight, val yinYang: YinYang, val sign: ZodiacSign) : Debility()
  data class Refrain_from_Venus_Jupiter(override val planet: Planet, val venusOrJupiter: Planet, val aspect: Aspect) : Debility()

  data class MutualDeception(override val planet: Planet,
                             val sign1: ZodiacSign,
                             val dig1: Dignity,
                             val p2: Point,
                             val sign2: ZodiacSign,
                             val dig2: Dignity) : Debility() {
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

sealed class Misc : IPlanetPattern {
  override val type: RuleType = RuleType.MISC

  /** 此星體 (mostly [Planet.MOON]) 目前處於空亡狀態 , 前一個準確交角資訊為 [exactAspectPrior] , 後一個準確交角資訊為 [exactAspectAfter]
   * */
  data class VoidCourse(override val planet: Planet,
                        val beginGmt : Double, val beginDegree : Double,
                        val endGmt : Double, val endDegree : Double,
                        val exactAspectPrior: IAspectData, val exactAspectAfter: IAspectData) : Misc()
}
