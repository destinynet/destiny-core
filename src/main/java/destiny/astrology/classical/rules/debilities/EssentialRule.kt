/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:48:47
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.classical.EssentialDefaultImpl
import destiny.astrology.classical.EssentialIF

abstract class EssentialRule : Rule() {

  /** 具備計算 Ptolemy's Table of Essential Dignities and Debilities 的所有介面  */
  var essentialImpl: EssentialIF = EssentialDefaultImpl()


}
