/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:56:19
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.EssentialDefaultImpl
import destiny.astrology.classical.EssentialUtils
import destiny.astrology.classical.IEssential
import destiny.astrology.classical.IEssentialDignities
import destiny.astrology.classical.rules.*
import destiny.astrology.classical.rules.Rule
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

  override  var rules : List<IRule>  = defaultRules

  fun getComments2(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet , h , locale) }
      .filter { it != null }
      .map { it -> it!! }
      .toList()
  }

  override fun getComments(planet: Planet , h:Horoscope , locale: Locale) : List<String> {
    return predicates
      .mapNotNull { it.getRule(planet , h) }
      .map { rule -> RuleTranslator.getDescriptor(rule).getDescription(locale) }
  }

  private val predicates: List<AbstractRulePredicate<Rule>>
    get() {
      val essentialImpl : IEssential = EssentialDefaultImpl()
      val utils = EssentialUtils(dayNightImpl)
      utils.setEssentialImpl(essentialImpl)
      return listOf(
        RulerRredicate(),
        ExaltPredicate(),
        TermPredicate(),
        TriplicityPredicate(dayNightImpl),
        BeneficialMutualReceptionPredicate(utils)
      )
    }



}
