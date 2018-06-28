/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:09:26
 */
package destiny.astrology.classical.rules

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import java.util.*
import java.util.function.Predicate

enum class RuleType {
  ESSENTIAL,
  ACCIDENTAL,
  DEBILITY
}


interface IRule : Predicate<Pair<Planet, IHoroscopeModel>> {

  val name: String

  val type : RuleType

  override fun test(t: Pair<Planet, IHoroscopeModel>): Boolean {
    return isApplicable(t.first, t.second)
  }

  fun isApplicable(planet: Planet, h: IHoroscopeModel): Boolean

  fun getName(locale: Locale): String

  /** 取得某 Locale 之下的註解  */
  fun getComment(planet: Planet, h: IHoroscopeModel, locale: Locale): String?

  fun getComment(planet: Planet, h: IHoroscopeModel): String? {
    return getComment(planet, h, Locale.getDefault())
  }
}
