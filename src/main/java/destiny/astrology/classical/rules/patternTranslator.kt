package destiny.astrology.classical.rules

import destiny.astrology.Aspect.*
import destiny.astrology.FixedStar
import destiny.astrology.LunarNode
import destiny.astrology.Planet.*
import destiny.core.DayNight
import destiny.core.Descriptive
import java.io.Serializable


class EssentialDignityDescriptor(rule: EssentialDignity, key: String, parameters: List<Any>) :
  AbstractPlanetPatternDescriptor<EssentialDignity>(rule, key, parameters), Serializable {
  override val resource: String = "destiny.astrology.classical.rules.EssentialDignities"
}

class AccidentalDignityDescriptor(rule: AccidentalDignity, key: String, parameters: List<Any>) :
  AbstractPlanetPatternDescriptor<AccidentalDignity>(rule, key, parameters), Serializable {
  override val resource: String = "destiny.astrology.classical.rules.AccidentalDignities"
}

class DebilityDescriptor(rule: Debility , key: String, parameters: List<Any>) : AbstractPlanetPatternDescriptor<Debility>(
  rule, key, parameters) , Serializable {
  override val resource: String = "destiny.astrology.classical.rules.Debilities"
}

object patternTranslator {

  fun getDescriptor(rule: IPlanetPattern): Descriptive {
    return when (rule) {
      is EssentialDignity -> when (rule) {
        is EssentialDignity.Ruler -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.sign))
        is EssentialDignity.Exaltation -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.sign))
        is EssentialDignity.Triplicity -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.sign, rule.dayNight))
        is EssentialDignity.Term -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.lngDeg))
        is EssentialDignity.Face -> EssentialDignityDescriptor(rule, "comment", listOf(rule.planet, rule.lngDeg))
        is EssentialDignity.MutualReception -> EssentialDignityDescriptor(rule, "comment",
          listOf(rule.planet, rule.sign1, rule.dig2, rule.p2, rule.sign2, rule.dig1, rule.dig2))

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
        is AccidentalDignity.Partile_Conj_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter , CONJUNCTION))
        is AccidentalDignity.Partile_Conj_North_Node -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.node , CONJUNCTION))
        is AccidentalDignity.Partile_Trine_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter , TRINE))
        is AccidentalDignity.Partile_Sextile_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter , SEXTILE))
        is AccidentalDignity.Partile_Conj_Regulus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , FixedStar.REGULUS , CONJUNCTION))
        is AccidentalDignity.Partile_Conj_Spica -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , FixedStar.SPICA , CONJUNCTION))
        is AccidentalDignity.JoyHouse -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.house))
        is AccidentalDignity.Hayz -> {
          if (rule.dayNight == DayNight.DAY) {
            AccidentalDignityDescriptor(rule , "commentDay" , listOf(rule.planet , rule.sign))
          } else {
            AccidentalDignityDescriptor(rule , "commentNight" , listOf(rule.planet , rule.sign))
          }
        }
        is AccidentalDignity.Besieged_Jupiter_Venus -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , VENUS , JUPITER))
        is AccidentalDignity.Translation_of_Light -> {
          if (rule.aspect != null)
            AccidentalDignityDescriptor(rule , "commentAspect" , listOf(rule.planet , rule.from , rule.to , rule.deg , rule.aspect))
          else
            AccidentalDignityDescriptor(rule , "commentUnaspect" , listOf(rule.planet , rule.from , rule.to , rule.deg))
        }
        is AccidentalDignity.Collection_of_Light -> {
          val p1 = rule.twoPlanets[0]
          val p2 = rule.twoPlanets[1]
          AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , p1 , p2 , rule.angle))
        }

        is AccidentalDignity.Refrain_from_Mars_Saturn -> AccidentalDignityDescriptor(rule , "comment" , listOf(rule.planet , rule.marsOrSaturn , rule.aspect))
      }
      is Debility -> when(rule) {
        is Debility.Detriment -> DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.sign))
        is Debility.Fall -> DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.sign))
        is Debility.Peregrine -> DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.House_12 ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.House_6_8 ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.house))
        is Debility.Retrograde ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.Slower ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.Occidental ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.Oriental ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        Debility.Moon_Decrease_Light ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.Combustion ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.Sunbeam ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet))
        is Debility.Partile_Conj_Mars_Saturn ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.marsOrSaturn , CONJUNCTION))
        is Debility.Partile_Conj_South_Node -> DebilityDescriptor(rule, "comment", listOf(rule.planet, LunarNode.SOUTH_MEAN, CONJUNCTION))
        is Debility.Besieged_Mars_Saturn ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet , MARS , SATURN))
        is Debility.Partile_Oppo_Mars_Saturn ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.marsOrSaturn , OPPOSITION))
        is Debility.Partile_Square_Mars_Saturn ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.marsOrSaturn , SQUARE))
        is Debility.Conj_Algol ->  DebilityDescriptor(rule , "comment" , listOf(rule.planet , FixedStar.ALGOL , CONJUNCTION))
        is Debility.Out_of_Sect -> {
          if (rule.dayNight == DayNight.DAY) {
            DebilityDescriptor(rule , "commentNight" , listOf(rule.planet , rule.sign))
          } else {
            DebilityDescriptor(rule , "commentDay" , listOf(rule.planet , rule.sign))
          }
        }
        is Debility.Refrain_from_Venus_Jupiter -> DebilityDescriptor(rule , "comment" , listOf(rule.planet , rule.venusOrJupiter , rule.aspect))
        is Debility.MutualDeception -> DebilityDescriptor(rule, "comment", listOf(rule.planet, rule.sign1, rule.dig2, rule.p2, rule.sign2, rule.dig1, rule.dig2))
      }
      else -> {
        throw RuntimeException("Not Supported : $rule")
      }
    }
  }
}
