/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.IMutualData
import destiny.astrology.classical.MutualData
import destiny.core.DayNight

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
  data class BeneficialMutualReception(override val planet: Planet, val dig1: Dignity,
                                       val p2: Point, val dig2: Dignity) :
    EssentialDignity(BeneficialMutualReception::class.java.simpleName), IMutualData by MutualData(planet , dig1 , p2 , dig2)
}

sealed class AccidentalDignity(override val name: String,
                               override val notes: String? = null) : IPlanetPattern {

  override val type: RuleType = RuleType.ACCIDENTAL

  data class House_1_10(override val planet: Planet , val house : Int) : AccidentalDignity(House_1_10::class.java.simpleName)
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
  data class Partile_Conj_Jupiter_Venus(override val planet: Planet , val venusOrJupiter: Planet) : AccidentalDignity(Partile_Conj_Jupiter_Venus::class.java.simpleName)
  data class Partile_Conj_North_Node(override val planet: Planet) : AccidentalDignity(Partile_Conj_North_Node::class.java.simpleName)
  data class Partile_Trine_Jupiter_Venus(override val planet: Planet) : AccidentalDignity(Partile_Trine_Jupiter_Venus::class.java.simpleName)
  data class Partile_Sextile_Jupiter_Venus(override val planet: Planet) : AccidentalDignity(Partile_Sextile_Jupiter_Venus::class.java.simpleName)
  data class Partile_Conj_Regulus(override val planet: Planet) : AccidentalDignity(Partile_Conj_Regulus::class.java.simpleName)
  data class Partile_Conj_Spica(override val planet: Planet) : AccidentalDignity(Partile_Conj_Spica::class.java.simpleName)
  data class JoyHouse(override val planet: Planet) : AccidentalDignity(JoyHouse::class.java.simpleName)
  data class Hayz(override val planet: Planet) : AccidentalDignity(Hayz::class.java.simpleName)
  data class Besieged_Jupiter_Venus(override val planet: Planet) : AccidentalDignity(Besieged_Jupiter_Venus::class.java.simpleName)
  data class Translation_of_Light(override val planet: Planet) : AccidentalDignity(Translation_of_Light::class.java.simpleName)
  data class Collection_of_Light(override val planet: Planet) : AccidentalDignity(Collection_of_Light::class.java.simpleName)
  data class Refrain_from_Mars_Saturn(override val planet: Planet) : AccidentalDignity(Refrain_from_Mars_Saturn::class.java.simpleName)
}

//
///** p1 以 dig1 的能量招待 (接納) p2 , p2 以 dig2 的能量招待 (接納) p1 */
//sealed class Mutual(private val p1: Planet, private val dig1: Dignity,
//                    private val p2: Planet, private val dig2: Dignity,
//                    override val notes: String? = null) : IMutualPattern, IMutualData by MutualData(p1, dig1, p2, dig2) {
//
//  override val name: String
//    get() = javaClass.simpleName
//
//  /** 好的能量，互相接待 */
//  sealed class Reception(p1: Planet, dig1: Dignity, p2: Planet, dig2: Dignity) : Mutual(p1, dig1, p2, dig2) {
//    class Equal(p1: Planet, sign1: ZodiacSign,
//                p2: Planet, sign2: ZodiacSign, dignity: Dignity) : Reception(p1, dignity, p2, dignity), IMutualData by MutualDataWithSign(p1, sign1, dignity, p2, sign2, dignity)
//
//    class Mixed(p1: Planet, sign1: ZodiacSign, dig1: Dignity,
//                p2: Planet, sign2: ZodiacSign, dig2: Dignity) : Reception(p1, dig1, p2, dig2), IMutualData by MutualDataWithSign(p1, sign1, dig1, p2, sign2, dig2)
//  }
//
//  /** 互相踩對方痛腳 */
//  sealed class Deception(p1: Planet, dig1: Dignity, p2: Planet, dig2: Dignity) : Mutual(p1, dig1, p2, dig2) {
//    class Equal(p1: Planet, sign1: ZodiacSign,
//                p2: Planet, sign2: ZodiacSign, dignity: Dignity) : Deception(p1, dignity, p2, dignity)
//
//    class Mixed(p1: Planet, sign1: ZodiacSign, dig1: Dignity,
//                p2: Planet, sign2: ZodiacSign, dig2: Dignity) : Deception(p1, dig1, p2, dig2)
//  }
//}
//
///**
// * p1 與 p2 透過 相同的 [dignity] 互相接納
// * 僅適用於 [Dignity.RULER] , [Dignity.EXALTATION] , [Dignity.TRIPLICITY] , [Dignity.FALL] , [Dignity.DETRIMENT]
// * 剩下的 [Dignity.TERM] 以及 [Dignity.FACE] 需要「度數」，因此不適用
// * */
//@Deprecated("可能用不到")
//sealed class MutualReception(val p1: Planet, val sign1: ZodiacSign,
//                             val p2: Planet, val sign2: ZodiacSign,
//                             val dignity: Dignity, override val notes: String? = null) : IMutualPattern, IMutualData by MutualData(p1, dignity, p2, dignity) {
//
//  override val name: String = javaClass.simpleName
//
//  /** p1 飛至 sign1 , sign1 的主人是 p2 , p2 飛至 sign2 , sign2 的主人是 p1 .  則 , p1 , p2 透過 [Dignity.RULER] 互容 */
//  class ByRuler(p1: Planet, sign1: ZodiacSign, p2: Planet, sign2: ZodiacSign) : MutualReception(p1, sign1, p2, sign2, Dignity.RULER)
//
//  class ByExalt(p1: Planet, sign1: ZodiacSign, p2: Planet, sign2: ZodiacSign) : MutualReception(p1, sign1, p2, sign2, Dignity.EXALTATION)
//
//  class ByTriplicity(p1: Planet, sign1: ZodiacSign, p2: Planet, sign2: ZodiacSign) : MutualReception(p1, sign1, p2, sign2, Dignity.TRIPLICITY)
//
//  /** deg1 , deg2 指的是「黃道帶」上的度數 , 並非是「該星座」的度數 */
//  class ByTerm(p1: Planet, sign1: ZodiacSign, deg1: Double, p2: Planet, sign2: ZodiacSign, deg2: Double) : MutualReception(p1, sign1, p2, sign2, Dignity.TERM)
//
//  /** deg1 , deg2 指的是「黃道帶」上的度數 , 並非是「該星座」的度數 */
//  class ByFace(p1: Planet, sign1: ZodiacSign, deg1: Double, p2: Planet, sign2: ZodiacSign, deg2: Double) : MutualReception(p1, sign1, p2, sign2, Dignity.FACE)
//}
