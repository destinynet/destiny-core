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

enum class RuleType {
  EssentialDignity,
  AccidentalDignity,
  Debility
}

sealed class Rule(val type: RuleType , val parent:Rule? = null) {

  data class Ruler(val planet: Planet, val sign: ZodiacSign) : Rule(RuleType.EssentialDignity)
  data class Exalt(val planet: Planet, val sign: ZodiacSign) : Rule(RuleType.EssentialDignity)
  data class Term(val planet: Planet, val lngDeg: Double) : Rule(RuleType.EssentialDignity)
  data class Face(val planet: Planet, val lngDeg: Double) : Rule(RuleType.EssentialDignity)
  data class Triplicity(val planet: Planet, val sign: ZodiacSign, val dayNight: DayNight) : Rule(RuleType.EssentialDignity)


  data class BeneficialMutualReception(val planet: Planet, val sign1: ZodiacSign, val dig1: Dignity,
                                       val planet2: Planet, val sign2: ZodiacSign, val dig2: Dignity) : Rule(RuleType.EssentialDignity)

}

sealed class Mutual(type: RuleType) : Rule(type) {
  data class MutualRuler(val planet: Planet , val sign1 : ZodiacSign , val planet2: Planet , val sign2: ZodiacSign) : Mutual(RuleType.EssentialDignity)
  data class MutualExalt(val planet: Planet , val sign1 : ZodiacSign , val planet2: Planet , val sign2: ZodiacSign) : Mutual(RuleType.EssentialDignity)
  data class MutualFall(val planet: Planet , val sign1 : ZodiacSign , val planet2: Planet , val sign2: ZodiacSign) : Mutual(RuleType.Debility)
  data class MutualDetriment(val planet: Planet , val sign1 : ZodiacSign , val planet2: Planet , val sign2: ZodiacSign) : Mutual(RuleType.Debility)
}
