/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:22:13
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.IBesieged
import destiny.astrology.IHoro
import destiny.astrology.Planet
import destiny.astrology.classical.IAccidentalDignities
import destiny.astrology.classical.ICollectionOfLight
import destiny.astrology.classical.IRefranation
import destiny.astrology.classical.ITranslationOfLight
import destiny.astrology.classical.rules.IRule
import java.io.Serializable
import java.util.*

class AccidentalDignitiesBean(private val dayNightImpl: DayNightDifferentiator,
                              private val translationOfLightImpl: ITranslationOfLight,
                              private val collectionOfLightImpl: ICollectionOfLight,
                              private val refranationImpl: IRefranation,
                              private val besiegedImpl: IBesieged) : IAccidentalDignities, Serializable {


  override val rules: List<IRule> by lazy {
    listOf(
      House_1_10()
      , House_4_7_11()
      , House_2_5()
      , House_9()
      , House_3()
      , Direct()
      , Swift()
      , Oriental()
      , Occidental()
      , Moon_Increase_Light()
      , Free_Combustion()
      , Cazimi()
      , Partile_Conj_Jupiter_Venus()
      , Partile_Conj_North_Node()
      , Partile_Trine_Jupiter_Venus()
      , Partile_Sextile_Jupiter_Venus()
      , Partile_Conj_Regulus()
      , Partile_Conj_Spica()
      , JoyHouse()
      , Hayz(dayNightImpl)
      , Besieged_Jupiter_Venus(besiegedImpl)
      , Translation_of_Light(translationOfLightImpl)
      , Collection_of_Light(collectionOfLightImpl)
      , Refrain_from_Mars_Saturn(refranationImpl)
    )
  }

  override fun getComments(planet: Planet, h: IHoro, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet, h, locale) }
      .filter { it != null }
      .map { it -> it!! }
      .toList()
  }


}
