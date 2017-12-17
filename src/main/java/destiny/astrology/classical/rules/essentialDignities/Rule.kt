/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:35:27
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.classical.EssentialDefaultImpl
import destiny.astrology.classical.IEssential
import destiny.astrology.classical.rules.AbstractRule

abstract class Rule : AbstractRule(resource), Applicable {

  /** 具備計算 Ptolemy's Table of Essential Dignities and Deblities 的所有介面  */
  var essentialImpl: IEssential = EssentialDefaultImpl()

  companion object {
    private val resource = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities"
  }


}
