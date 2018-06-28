/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:35:27
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.classical.IRuler
import destiny.astrology.classical.RulerPtolemyImpl
import destiny.astrology.classical.rules.AbstractRule
import destiny.astrology.classical.rules.RuleType

abstract class EssentialRule : AbstractRule(resource, RuleType.ESSENTIAL) {

  val rulerImpl : IRuler = RulerPtolemyImpl()

  companion object {
    private const val resource = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities"
  }


}
