/**
 * @author smallufo
 * Created on 2007/12/31 at 下午 10:42:30
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.IBesieged
import destiny.astrology.Planet
import destiny.astrology.classical.IDebilities
import destiny.astrology.classical.IRefranation
import destiny.astrology.classical.rules.IRule
import java.io.Serializable
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject

class DebilitiesBean : IDebilities, Serializable {

  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作  */
  @Inject
  private lateinit var dayNightImpl: DayNightDifferentiator

  @Inject
  private lateinit var besiegedImpl: IBesieged

  @Inject
  private lateinit var refranationImpl: IRefranation

  override lateinit var rules: List<IRule>


  private val defaultRules: List<IRule>
    get() {
      return listOf(
          Detriment()
        , Fall()
        , Peregrine(dayNightImpl)
        , House_12()
        , House_6_8()
        , Retrograde()
        , Slower()
        , Occidental()
        , Oriental()
        , Moon_Decrease_Light()
        , Combustion()
        , Sunbeam()
        , Partile_Conj_Mars_Saturn()
        , Partile_Conj_South_Node()
        , Besieged_Mars_Saturn(besiegedImpl)
        , Partile_Oppo_Mars_Saturn()
        , Partile_Square_Mars_Saturn()
        , Conj_Algol()
        , Out_of_Sect(dayNightImpl)
        , MutualDeception()
        , Refrain_from_Venus_Jupiter(refranationImpl)
      )
    }

  @PostConstruct
  fun init() {
    this.rules = defaultRules
  }

  override fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet , h , locale) }
      .filter { it != null }
      .map { it -> it!! }
      .toList()
  }


}
