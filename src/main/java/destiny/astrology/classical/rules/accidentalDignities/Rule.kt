/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 2:45:26
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.classical.rules.AbstractRule

abstract class Rule : AbstractRule(resource), Applicable {
  companion object {
    private const val resource = "destiny.astrology.classical.rules.accidentalDignities.AccidentalDignities"
  }

}
