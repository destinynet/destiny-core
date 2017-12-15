/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:22:13
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.*
import destiny.astrology.classical.AccidentalDignitiesIF
import destiny.astrology.classical.ICollectionOfLight
import destiny.astrology.classical.RefranationIF
import destiny.astrology.classical.TranslationOfLightIF
import destiny.astrology.classical.rules.RuleIF
import java.io.Serializable
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject

class AccidentalDignitiesBean : AccidentalDignitiesIF, Serializable {
  /** 計算兩星體呈現某交角的時間 , 內定採用 SwissEph 的實作  */
  @Inject
  private lateinit var relativeTransitImpl: IRelativeTransit

  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作  */
  @Inject
  lateinit var dayNightImpl: DayNightDifferentiator

  @Inject
  private lateinit var translationOfLightImpl: TranslationOfLightIF

  @Inject
  private lateinit var collectionOfLightImpl: ICollectionOfLight

  @Inject
  private lateinit var refranationImpl: RefranationIF

  @Inject
  private lateinit var besiegedImpl: IBesieged

  private var rules: List<RuleIF> = ArrayList()

  /** 內定的 Rules  */
  private val defaultRules: List<RuleIF>
    get() {
      val list = ArrayList<RuleIF>()
      list.add(House_1_10())
      list.add(House_4_7_11())
      list.add(House_2_5())
      list.add(House_9())
      list.add(House_3())
      list.add(Direct())
      list.add(Swift())
      list.add(Oriental())
      list.add(Occidental())
      list.add(Moon_Increase_Light())
      list.add(Free_Combustion())
      list.add(Cazimi())
      list.add(Partile_Conj_Jupiter_Venus())
      list.add(Partile_Conj_North_Node())
      list.add(Partile_Trine_Jupiter_Venus())
      list.add(Partile_Sextile_Jupiter_Venus())
      list.add(Partile_Conj_Regulus())
      list.add(Partile_Conj_Spica())
      list.add(JoyHouse())
      list.add(Hayz(dayNightImpl))
      list.add(Besieged_Jupiter_Venus(besiegedImpl))
      list.add(Translation_of_Light(translationOfLightImpl))
      list.add(Collection_of_Light(collectionOfLightImpl))
      list.add(Refrain_from_Mars_Saturn(refranationImpl))
      return list
    }

  @PostConstruct
  fun init() {
    this.rules = defaultRules
  }

  override fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet , h , locale) }
      .filter{ it.isPresent}
      .map { it.get() }
      .toList()
  }

  override fun getRules(): List<RuleIF> {
    return rules
  }

  fun setRelativeTransitImpl(relativeTransitImpl: IRelativeTransit) {
    this.relativeTransitImpl = relativeTransitImpl
  }


}
