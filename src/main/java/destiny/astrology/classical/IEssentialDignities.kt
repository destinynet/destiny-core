/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 2:27:21
 */
package destiny.astrology.classical

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.rules.IRule
import java.util.*

/**
 * 取得某行星 Planet 的 Essential Dignities 強度 <BR></BR>
 * 內定實作是 EssentialDignityDefaultImpl
 */
interface IEssentialDignities {

  val rules: List<IRule>

  fun getComments(planet: Planet, h: Horoscope, locale: Locale): List<String>
}
