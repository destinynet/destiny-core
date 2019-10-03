package destiny.astrology.classical.rules

import destiny.core.Descriptive
import java.io.Serializable
import java.util.*


class EssentialDignityDescriptor(rule: EssentialDignity, val key: String, private val parameters: List<Any>) :
  AbstractRuleDescriptor<EssentialDignity>(rule), Serializable {
  override val resource: String = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities"
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return key to parameters
  }
}

class AccidentalDignityDescriptor(rule: AccidentalDignity, val key: String, private val parameters: List<Any>) :
  AbstractRuleDescriptor<AccidentalDignity>(rule), Serializable {
  override val resource: String = "destiny.astrology.classical.rules.accidentalDignities"
  override fun getCommentParameters(locale: Locale): Pair<String, List<Any>> {
    return key to parameters
  }
}


object RuleTranslator {

  fun getDescriptor(rule: IPlanetPattern): Descriptive {
    return when (rule) {
      is EssentialDignity -> when (rule) {
        is EssentialDignity.Ruler -> EssentialDignityDescriptor(rule, "commentBasic", listOf(rule.planet, rule.sign))
        is EssentialDignity.Exaltation -> EssentialDignityDescriptor(rule, "commentBasic", listOf(rule.planet, rule.sign))
        is EssentialDignity.Triplicity -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.sign, rule.dayNight))
        is EssentialDignity.Term -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.lngDeg))
        is EssentialDignity.Face -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.lngDeg))
        is EssentialDignity.BeneficialMutualReception -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.p2, rule.dig1, rule.dig2))
      }
      is AccidentalDignity -> when(rule) {
        is AccidentalDignity.House_1_10 -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.house))
        is AccidentalDignity.House_4_7_11 -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.house))
        is AccidentalDignity.House_2_5 -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.house))
        is AccidentalDignity.House_9 -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.House_3 -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Direct -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Swift -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Oriental -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Occidental -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        AccidentalDignity.Moon_Increase_Light -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Free_Combustion -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Cazimi -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Partile_Conj_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter))
        is AccidentalDignity.Partile_Conj_North_Node -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Partile_Trine_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter))
        is AccidentalDignity.Partile_Sextile_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter))
        is AccidentalDignity.Partile_Conj_Regulus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Partile_Conj_Spica -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.JoyHouse -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.house))
        is AccidentalDignity.Hayz -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.sign))
        is AccidentalDignity.Besieged_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet))
        is AccidentalDignity.Translation_of_Light -> {
          if (rule.aspect != null)
            AccidentalDignityDescriptor(rule , "commentAspect" , listOf(rule.planet , rule.from , rule.to , rule.aspect))
          else
            AccidentalDignityDescriptor(rule , "commentUnaspect" , listOf(rule.planet , rule.from , rule.to))
        }
        is AccidentalDignity.Collection_of_Light -> {
          val p1 = rule.twoPlanets[0]
          val p2 = rule.twoPlanets[1]
          AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , p1 , p2 , rule.angle))
        }

        is AccidentalDignity.Refrain_from_Mars_Saturn -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.marsOrSaturn , rule.aspect))
      }
      else -> TODO()
    }
  }
}
