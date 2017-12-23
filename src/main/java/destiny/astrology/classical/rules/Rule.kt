/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.Planet
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity

/**
 * 行星的 25種狀態
 * https://site.douban.com/183595/widget/notes/192509582/note/600376742/
 */

sealed class Rule(val parent: Rule? = null) {

  data class Ruler(val planet: Planet, val sign: ZodiacSign) : Rule()
  data class Exalt(val planet: Planet, val sign: ZodiacSign) : Rule()
  data class Term(val planet: Planet, val lngDeg: Double) : Rule()
  data class Face(val planet: Planet, val lngDeg: Double) : Rule()
  data class Triplicity(val planet: Planet, val sign: ZodiacSign, val dayNight: DayNight) : Rule()

  /** p1 以 dig1 的能量招待 (接納) p2 , p2 以 dig2 的能量招待 (接納) p1 */
  data class MutReception(val p1: Point , val dig1:Dignity , val p2: Point , val dig2: Dignity): Rule()

}

/**
 * p1 與 p2 透過 dignity 互相接納
 * 僅適用於 [Dignity.RULER] , [Dignity.EXALTATION] , [Dignity.TRIPLICITY] , [Dignity.FALL] , [Dignity.DETRIMENT]
 * 剩下的 [Dignity.TERM] 以及 [Dignity.FACE] 需要「度數」，因此不適用
 * */
sealed class MutualReception(val p1 : Point , val sign1: ZodiacSign , val p2 : Point , val sign2: ZodiacSign , val dignity: Dignity) : Rule() {

  /** p1 飛至 sign1 , sign1 的主人是 p2 , p2 飛至 sign2 , sign2 的主人是 p1 .  則 , p1 , p2 透過 [Dignity.RULER] 互容 */
  class ByRuler(p1: Point , sign1: ZodiacSign , p2: Point , sign2: ZodiacSign) : MutualReception(p1 , sign1 , p2 , sign2 , Dignity.RULER)

  class ByExalt(p1: Point , sign1: ZodiacSign , p2: Point , sign2: ZodiacSign) : MutualReception(p1 , sign1 , p2 , sign2 , Dignity.EXALTATION)

  class ByTriplicity(p1: Point , sign1: ZodiacSign , p2: Point , sign2: ZodiacSign) : MutualReception(p1 , sign1 , p2 , sign2 , Dignity.TRIPLICITY)

  /** deg1 , deg2 指的是「黃道帶」上的度數 , 並非是「該星座」的度數 */
  class ByTerm(p1: Point , sign1: ZodiacSign , deg1 : Double , p2: Point , sign2: ZodiacSign , deg2 : Double) : MutualReception(p1 , sign1 , p2 , sign2 , Dignity.TERM)

  /** deg1 , deg2 指的是「黃道帶」上的度數 , 並非是「該星座」的度數 */
  class ByFace(p1: Point , sign1: ZodiacSign , deg1: Double , p2: Point , sign2: ZodiacSign , deg2: Double) : MutualReception(p1 , sign1 , p2 , sign2 , Dignity.FACE)
}