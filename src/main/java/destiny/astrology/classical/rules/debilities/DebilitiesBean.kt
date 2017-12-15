/**
 * @author smallufo
 * Created on 2007/12/31 at 下午 10:42:30
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.IBesieged
import destiny.astrology.Planet
import destiny.astrology.classical.DebilitiesIF
import destiny.astrology.classical.RefranationIF
import destiny.astrology.classical.rules.RuleIF
import java.io.Serializable
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject

class DebilitiesBean : DebilitiesIF, Serializable {

  /** 計算白天黑夜的實作 , 內定採用 SwissEph 的實作  */
  @Inject
  private lateinit var dayNightImpl: DayNightDifferentiator

  @Inject
  private lateinit var besiegedImpl: IBesieged

  @Inject
  private lateinit var refranationImpl: RefranationIF

  private var rules: List<RuleIF> = ArrayList()


  private val defaultRules: List<RuleIF>
    get() {
      val list = ArrayList<RuleIF>()
      list.add(Detriment())
      list.add(Fall())
      list.add(Peregrine(dayNightImpl))
      list.add(House_12())
      list.add(House_6_8())
      list.add(Retrograde())
      list.add(Slower())
      list.add(Occidental())
      list.add(Oriental())
      list.add(Moon_Decrease_Light())
      list.add(Combustion())
      list.add(Sunbeam())
      list.add(Partile_Conj_Mars_Saturn())
      list.add(Partile_Conj_South_Node())
      list.add(Besieged_Mars_Saturn(besiegedImpl))
      list.add(Partile_Oppo_Mars_Saturn())
      list.add(Partile_Square_Mars_Saturn())
      list.add(Conj_Algol())
      list.add(Out_of_Sect(dayNightImpl))
      list.add(MutualDeception(dayNightImpl))
      list.add(Refrain_from_Venus_Jupiter(refranationImpl))
      return list
    }

  @PostConstruct
  fun init() {
    this.rules = defaultRules
  }

  override fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet, h, locale) }
      .filter { it.isPresent }
      .map { it.get() }
      .toList()
  }

  override fun getRules(): List<RuleIF> {
    return rules
  }

}
