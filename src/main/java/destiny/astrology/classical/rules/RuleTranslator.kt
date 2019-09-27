package destiny.astrology.classical.rules

import destiny.core.Descriptive
import java.util.*


class RulerDescriptor(rule: EssentialDignity.Ruler) : AbstractRuleDescriptor<EssentialDignity.Ruler>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String , List<Any>> {
    return "comment" to listOf(rule.planet, rule.sign)
  }
}

class ExaltDescriptor(rule : EssentialDignity.Exalt) : AbstractRuleDescriptor<EssentialDignity.Exalt>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.sign)
  }
}

class TermDescriptor(rule : EssentialDignity.Term) : AbstractRuleDescriptor<EssentialDignity.Term>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.lngDeg)
  }
}

class TriplicityDescriptor(rule : EssentialDignity.Triplicity) : AbstractRuleDescriptor<EssentialDignity.Triplicity>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.sign , rule.dayNight)
  }
}

class FaceDescriptor(rule : EssentialDignity.Face) : AbstractRuleDescriptor<EssentialDignity.Face>(rule) {
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return "comment" to listOf(rule.planet , rule.lngDeg)
  }
}




object RuleTranslator {
  fun getDescriptor(rule : EssentialDignity): Descriptive {
    return when (rule) {
      is EssentialDignity.Ruler -> RulerDescriptor(rule)
      is EssentialDignity.Exalt -> ExaltDescriptor(rule)
      is EssentialDignity.Term -> TermDescriptor(rule)
      is EssentialDignity.Triplicity -> TriplicityDescriptor(rule)
      is EssentialDignity.Face -> FaceDescriptor(rule)

      is Mutual -> TODO()

//      is MutualReception -> {
//        when (rule) {
//          is MutualReception.ByRuler -> TODO()
//          is MutualReception.ByExalt -> TODO()
//          is MutualReception.ByTriplicity -> TODO()
//          is MutualReception.ByTerm -> TODO()
//          is MutualReception.ByFace -> TODO()
//        }
//      }
    }
  }
}
