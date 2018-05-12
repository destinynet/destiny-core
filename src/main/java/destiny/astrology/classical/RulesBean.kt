/**
 * Created by smallufo on 2018-05-12.
 */
package destiny.astrology.classical

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.rules.IRule
import destiny.astrology.classical.rules.debilities.DebilitiesBean
import java.util.*

class RulesBean(
  private val essentialDignitiesImpl : IEssentialDignities ,
  private val accidentalDignitiesImpl : IAccidentalDignities,
  private val debilitiesBean : DebilitiesBean) {


  fun getComments(planet : Planet, h : IHoroscopeModel , locale : Locale) : List<String> {
    val list1 = essentialDignitiesImpl.getComments(planet, h, locale)
    val list2 = accidentalDignitiesImpl.getComments(planet, h, locale)
    val list3 = debilitiesBean.getComments(planet, h, locale)
    return list1 + list2 + list3
  }

  fun getRuleAndComments(planet: Planet , h: IHoroscopeModel , locale: Locale) : List<Pair<IRule, String>> {
    val rules1 = essentialDignitiesImpl.rules
    val rules2 = accidentalDignitiesImpl.rules
    val rules3 = debilitiesBean.rules
    return (rules1 + rules2 + rules3).map { rule ->
      Pair(rule , rule.getComment(planet , h , locale))
    }.filter { pair -> pair.second != null }
      .map { p -> Pair(p.first , p.second!!) }
      .toList()
  }

}