package destiny.astrology.classical.rules

import destiny.core.Descriptive
import java.io.Serializable
import java.util.*


class EssentialDignityDescriptor(rule: EssentialDignity, val key: String, private val parameters: List<Any>) : AbstractRuleDescriptor<EssentialDignity>(rule) , Serializable {
  override val resource: String = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities"
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return key to parameters
  }
}


object RuleTranslator {

  fun getDescriptor(rule: EssentialDignity): Descriptive {
    return when (rule) {
      is EssentialDignity.Ruler -> EssentialDignityDescriptor(rule, "commentBasic", listOf(rule.planet, rule.sign))
      is EssentialDignity.Exaltation -> EssentialDignityDescriptor(rule, "commentBasic", listOf(rule.planet, rule.sign))
      is EssentialDignity.Triplicity -> EssentialDignityDescriptor(rule, "comment",
                                                                   listOf(rule.planet, rule.sign, rule.dayNight))
      is EssentialDignity.Term -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.lngDeg))
      is EssentialDignity.Face -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.lngDeg))
      is EssentialDignity.BeneficialMutualReception -> EssentialDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.p2 , rule.dig1 , rule.dig2))
    }
  }
}
