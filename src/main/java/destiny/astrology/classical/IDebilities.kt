/**
 * @author smallufo
 * Created on 2007/12/20 at 下午 6:33:48
 */
package destiny.astrology.classical

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.rules.IRule
import java.util.*

/**
 * 取得某行星 Planet 的 Debilities 衰弱程度 <BR></BR>
 * 內定實作是 DebilitiesDefaultImpl
 */
interface IDebilities {

  val rules: List<IRule>

  fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String>
}
