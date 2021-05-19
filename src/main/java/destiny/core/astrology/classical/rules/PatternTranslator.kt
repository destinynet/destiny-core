package destiny.core.astrology.classical.rules

import destiny.core.DayNight
import destiny.core.Descriptive
import destiny.core.IPatternDescriptor
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.FixedStar
import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet.*
import destiny.tools.AbstractPropertyBasedPatternDescriptor
import java.io.Serializable


/**
 * 將 [IPlanetPattern] 轉譯成 [Descriptive] , 以利終端顯示
 */
abstract class AbstractPlanetPatternDescriptor<out T : IPlanetPattern>(pattern: IPlanetPattern,
                                                                       commentKey: String,
                                                                       parameters: List<Any>)
  : AbstractPropertyBasedPatternDescriptor(pattern, commentKey, parameters , "%.1f")


class EssentialDignityDescriptor(rule: EssentialDignity, key: String, parameters: List<Any>) :
  AbstractPlanetPatternDescriptor<EssentialDignity>(rule, key, parameters), Serializable {
  override val resource: String = "destiny.core.astrology.classical.rules.EssentialDignities"
}

class AccidentalDignityDescriptor(rule: AccidentalDignity, key: String, parameters: List<Any>) :
  AbstractPlanetPatternDescriptor<AccidentalDignity>(rule, key, parameters), Serializable {
  override val resource: String = "destiny.core.astrology.classical.rules.AccidentalDignities"
}

class DebilityDescriptor(rule: Debility, key: String, parameters: List<Any>) : AbstractPlanetPatternDescriptor<Debility>(
  rule, key, parameters) , Serializable {
  override val resource: String = "destiny.core.astrology.classical.rules.Debilities"
}

object PatternTranslator : IPatternDescriptor<IPlanetPattern>  {

  override fun getDescriptor(pattern: IPlanetPattern): Descriptive {
    return when (pattern) {
      is EssentialDignity -> when (pattern) {
        is EssentialDignity.Ruler -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is EssentialDignity.Exaltation -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is EssentialDignity.Triplicity -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign, pattern.dayNight))
        is EssentialDignity.Term -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.lngDeg.value))
        is EssentialDignity.Face -> EssentialDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.lngDeg.value))
        is EssentialDignity.MutualReception -> EssentialDignityDescriptor(pattern, "comment",
                                                                          listOf(pattern.planet, pattern.sign1, pattern.dig2, pattern.p2, pattern.sign2, pattern.dig1, pattern.dig2))

      }
      is AccidentalDignity -> when(pattern) {
        is AccidentalDignity.House_1_10 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.House_4_7_11 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.House_2_5 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.House_9 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.House_3 -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Direct -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Swift -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Oriental -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Occidental -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        AccidentalDignity.Moon_Increase_Light -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Free_Combustion -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Cazimi -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet))
        is AccidentalDignity.Partile_Conj_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, CONJUNCTION))
        is AccidentalDignity.Partile_Conj_North_Node -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.node, CONJUNCTION))
        is AccidentalDignity.Partile_Trine_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, TRINE))
        is AccidentalDignity.Partile_Sextile_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, SEXTILE))
        is AccidentalDignity.Partile_Conj_Regulus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, FixedStar.REGULUS, CONJUNCTION))
        is AccidentalDignity.Partile_Conj_Spica -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, FixedStar.SPICA, CONJUNCTION))
        is AccidentalDignity.JoyHouse -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is AccidentalDignity.Hayz -> {
          if (pattern.dayNight == DayNight.DAY) {
            AccidentalDignityDescriptor(pattern, "commentDay", listOf(pattern.planet, pattern.sign))
          } else {
            AccidentalDignityDescriptor(pattern, "commentNight", listOf(pattern.planet, pattern.sign))
          }
        }
        is AccidentalDignity.Besieged_Jupiter_Venus -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, VENUS, JUPITER))
        is AccidentalDignity.Translation_of_Light -> {
          if (pattern.aspectType != null)
            AccidentalDignityDescriptor(pattern, "commentAspect", listOf(pattern.planet, pattern.from, pattern.to, pattern.angle, pattern.aspectType))
          else
            AccidentalDignityDescriptor(pattern, "commentUnaspect", listOf(pattern.planet, pattern.from, pattern.to, pattern.angle))
        }
        is AccidentalDignity.Collection_of_Light -> {
          val p1 = pattern.twoPlanets[0]
          val p2 = pattern.twoPlanets[1]
          AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, p1, p2, pattern.angle))
        }

        is AccidentalDignity.Refrain_from_Mars_Saturn -> AccidentalDignityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, pattern.aspect))
      }
      is Debility -> when(pattern) {
        is Debility.Detriment -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is Debility.Fall -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign))
        is Debility.Peregrine -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.House_12 ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.House_6_8 ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.house))
        is Debility.Retrograde ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Slower ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Occidental ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Oriental ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        Debility.Moon_Decrease_Light ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Combustion ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Sunbeam ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet))
        is Debility.Partile_Conj_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, CONJUNCTION))
        is Debility.Partile_Conj_South_Node -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, LunarNode.SOUTH_MEAN, CONJUNCTION))
        is Debility.Besieged_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, MARS, SATURN))
        is Debility.Partile_Oppo_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, OPPOSITION))
        is Debility.Partile_Square_Mars_Saturn ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.marsOrSaturn, SQUARE))
        is Debility.Conj_Algol ->  DebilityDescriptor(pattern, "comment", listOf(pattern.planet, FixedStar.ALGOL, CONJUNCTION))
        is Debility.Out_of_Sect -> {
          if (pattern.dayNight == DayNight.DAY) {
            DebilityDescriptor(pattern, "commentNight", listOf(pattern.planet, pattern.sign))
          } else {
            DebilityDescriptor(pattern, "commentDay", listOf(pattern.planet, pattern.sign))
          }
        }
        is Debility.Refrain_from_Venus_Jupiter -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.venusOrJupiter, pattern.aspect))
        is Debility.MutualDeception -> DebilityDescriptor(pattern, "comment", listOf(pattern.planet, pattern.sign1, pattern.dig2, pattern.p2, pattern.sign2, pattern.dig1, pattern.dig2))
      }
      else -> {
        throw IllegalArgumentException("Not Supported : $pattern")
      }
    }
  }
}
