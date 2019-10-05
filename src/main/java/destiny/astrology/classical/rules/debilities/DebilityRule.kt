/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:40:57
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.classical.rules.AbstractRule
import destiny.astrology.classical.rules.RuleType

abstract class DebilityRule : AbstractRule(resource, RuleType.DEBILITY) {
  companion object {
    private const val resource = "destiny.astrology.classical.rules.Debilities"
  }

}
