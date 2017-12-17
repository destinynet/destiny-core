/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:22:13
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.*
import destiny.astrology.classical.IAccidentalDignities
import destiny.astrology.classical.ICollectionOfLight
import destiny.astrology.classical.IRefranation
import destiny.astrology.classical.ITranslationOfLight
import destiny.astrology.classical.rules.RuleIF
import java.io.Serializable
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject

class AccidentalDignitiesBean : IAccidentalDignities, Serializable {
  /** 計算兩星體呈現某交角的時間 , 內定採用 SwissEph 的實作  */
  @Inject
  private lateinit var relativeTransitImpl: IRelativeTransit

  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作  */
  @Inject
  lateinit var dayNightImpl: DayNightDifferentiator

  @Inject
  private lateinit var translationOfLightImpl: ITranslationOfLight

  @Inject
  private lateinit var collectionOfLightImpl: ICollectionOfLight

  @Inject
  private lateinit var refranationImpl: IRefranation

  @Inject
  private lateinit var besiegedImpl: IBesieged

  override lateinit var rules: List<RuleIF>

  /** 內定的 Rules  */
  private val defaultRules: List<RuleIF>
    get() {
      return listOf(
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

  @PostConstruct
  fun init() {
    this.rules = defaultRules
  }

  override fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getCommentOpt(planet , h , locale) }
      .filter{ it.isPresent}
      .map { it.get() }
      .toList()
  }


  fun setRelativeTransitImpl(relativeTransitImpl: IRelativeTransit) {
    this.relativeTransitImpl = relativeTransitImpl
  }


}
