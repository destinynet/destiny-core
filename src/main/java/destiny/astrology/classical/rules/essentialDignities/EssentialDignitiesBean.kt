/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:56:19
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.EssentialDignitiesIF
import destiny.astrology.classical.rules.RuleIF
import java.io.Serializable
import java.util.*

class EssentialDignitiesBean(
  /** 計算白天黑夜的實作   */
  private var dayNightImpl: DayNightDifferentiator) : EssentialDignitiesIF, Serializable {

  /** 內定的 Rules  */
  private val defaultRules: List<RuleIF>
    get() {
      return listOf(
        Ruler(dayNightImpl)
        , Exaltation(dayNightImpl)
        , MixedReception(dayNightImpl)
        , Triplicity(dayNightImpl)
        , Term()
        , Face()
      )
    }


  override fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getCommentOpt(planet, h, locale) }
      .filter { it.isPresent }
      .map { it.get() }
      .toList()
  }

  override fun getRules(): List<RuleIF> {
    return defaultRules
  }


}
