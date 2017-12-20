/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.Planet
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


  data class BeneficialMutualReception(val planet: Planet, val sign1: ZodiacSign, val dig1: Dignity,
                                       val planet2: Planet, val sign2: ZodiacSign, val dig2: Dignity) : Rule()

}

sealed class Mutual(val planet1: Planet, val sign1: ZodiacSign, val planet2: Planet, val sign2: ZodiacSign) : Rule() {
  class MutualGeneral(planet1: Planet, sign1: ZodiacSign, planet2: Planet, sign2: ZodiacSign) : Mutual(planet1 , sign1 , planet2 , sign2)
  class MutualExalt(planet1: Planet, sign1: ZodiacSign, planet2: Planet, sign2: ZodiacSign) : Mutual(planet1 , sign1 , planet2 , sign2)
  class MutualFall(planet1: Planet, sign1: ZodiacSign, planet2: Planet, sign2: ZodiacSign) : Mutual(planet1 , sign1 , planet2 , sign2 )
  class MutualDetriment(planet1: Planet, sign1: ZodiacSign, planet2: Planet, sign2: ZodiacSign) : Mutual(planet1 , sign1 , planet2 , sign2)
}

sealed class MutualReception(val planet1: Planet , val sign1: ZodiacSign , val planet2: Planet , val sign2: ZodiacSign) : Rule() {
  class BySign(planet1: Planet , sign1: ZodiacSign , planet2: Planet , sign2: ZodiacSign) : MutualReception(planet1 , sign1 , planet2 , sign2)
  class ByExalt(planet1: Planet , sign1: ZodiacSign , planet2: Planet , sign2: ZodiacSign) : MutualReception(planet1 , sign1 , planet2 , sign2)
}
