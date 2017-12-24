/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 4:56:19
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.*
import destiny.astrology.classical.rules.IRule
import java.io.Serializable
import java.util.*

class EssentialDignitiesBean(

  private val essentialImpl: IEssential,
  private val exaltImpl: IExaltation,
  private val triplicityImpl : ITriplicity,
  private val termImpl : ITerm,
  private val faceImpl : IFace,
  private var dayNightImpl: DayNightDifferentiator) : IEssentialDignities, Serializable {

  /** 內定的 Rules  */
  private val defaultRules: List<IRule>
    get() {
      return listOf(
        Ruler(essentialImpl)
        , Exaltation(essentialImpl, exaltImpl)
        , MixedReception(essentialImpl)
        , Triplicity(triplicityImpl , dayNightImpl)
        , Term(termImpl)
        , Face(faceImpl)
      )
    }

  override  var rules : List<IRule>  = defaultRules

  override fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet , h , locale) }
      .filter { it != null }
      .map { it -> it!! }
      .toList()
  }




}
