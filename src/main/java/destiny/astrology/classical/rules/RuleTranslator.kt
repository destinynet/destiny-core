package destiny.astrology.classical.rules

import destiny.core.Descriptive
import java.util.*


class RulerDescriptor(rule: Rule.Ruler) : AbstractRuleDescriptor<Rule.Ruler>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String , List<Any>> {
    return "comment" to listOf(rule.planet, rule.sign)
  }
}

class ExaltDescriptor(rule : Rule.Exalt) : AbstractRuleDescriptor<Rule.Exalt>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.sign)
  }
}

class TermDescriptor(rule : Rule.Term) : AbstractRuleDescriptor<Rule.Term>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.lngDeg)
  }
}

class TriplicityDescriptor(rule : Rule.Triplicity) : AbstractRuleDescriptor<Rule.Triplicity>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.sign , rule.dayNight)
  }
}

class FaceDescriptor(rule : Rule.Face) : AbstractRuleDescriptor<Rule.Face>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.lngDeg)
  }
}

class BeneficialMutualReceptionDescriptor(rule: Rule.BeneficialMutualReception) : AbstractRuleDescriptor<Rule.BeneficialMutualReception>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}

object RuleTranslator {
  fun getDescriptor(rule : Rule): Descriptive {
    return when (rule) {
      is Rule.Ruler -> RulerDescriptor(rule)
      is Rule.Exalt -> ExaltDescriptor(rule)
      is Rule.Term -> TermDescriptor(rule)
      is Rule.Triplicity -> TriplicityDescriptor(rule)
      is Rule.Face -> FaceDescriptor(rule)
      is Rule.BeneficialMutualReception -> BeneficialMutualReceptionDescriptor(rule)
    }
  }
}
