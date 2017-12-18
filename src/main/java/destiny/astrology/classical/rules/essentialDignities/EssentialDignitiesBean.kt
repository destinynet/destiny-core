/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:56:19
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.IEssentialDignities
import destiny.astrology.classical.rules.IRule
import java.io.Serializable
import java.util.*

class EssentialDignitiesBean(

  /** 計算白天黑夜的實作   */
  private var dayNightImpl: DayNightDifferentiator) : IEssentialDignities, Serializable {


  /** 內定的 Rules  */
  private val defaultRules: List<IRule>
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
      .map { it.getComment(planet , h , locale) }
      .filter { it != null }
      .map { it -> it!! }
      .toList()
  }

  override  var rules : List<IRule>  = defaultRules


}
