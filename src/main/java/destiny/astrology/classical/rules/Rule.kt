/**
 * Created by smallufo on 2017-12-18.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.Planet
import destiny.astrology.ZodiacSign

/**
 * 行星的 25種狀態
 * https://site.douban.com/183595/widget/notes/192509582/note/600376742/
 */

enum class RuleType {
  EssentialDignity,
  AccidentalDignity,
  Debility
}

sealed class Rule(val type: RuleType) {

  data class Ruler(val planet: Planet, val sign: ZodiacSign) : Rule(RuleType.EssentialDignity)
  data class Exalt(val planet: Planet, val sign: ZodiacSign) : Rule(RuleType.EssentialDignity)
  data class Term(val planet: Planet, val lngDeg: Double) : Rule(RuleType.EssentialDignity)
  data class Face(val planet: Planet, val lngDeg: Double) : Rule(RuleType.EssentialDignity)
  data class Triplicity(val planet: Planet, val sign: ZodiacSign, val dayNight: DayNight) : Rule(RuleType.EssentialDignity)

  data class BeneficialMutualReception(val planet: Planet , val sign1: ZodiacSign , val planet2: Planet , val sign2:ZodiacSign) : Rule(RuleType.EssentialDignity)
  //data class Fall(val planet: Planet, val sign: ZodiacSign) : Rule(RuleType.Debility)
}

