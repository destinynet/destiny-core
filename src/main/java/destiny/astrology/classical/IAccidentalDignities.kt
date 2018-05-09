/**
 * @author smallufo
 * Created on 2007/12/13 at 下午 4:58:47
 */
package destiny.astrology.classical

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.rules.IRule
import java.util.*

/**
 * 取得某行星 Planet 的 Accidental Dignities 強度
 */
interface IAccidentalDignities {

  val rules: List<IRule>

  fun getComments(planet: Planet, h: IHoroscopeModel, locale: Locale): List<String>
}
