/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:40:57
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.classical.rules.AbstractRule

abstract class Rule : AbstractRule(resource), Applicable {
  companion object {

    private val resource = "destiny.astrology.classical.rules.debilities.Debilities"
  }

}
