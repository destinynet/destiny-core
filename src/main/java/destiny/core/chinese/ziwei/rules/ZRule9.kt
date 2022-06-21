/**
 * Created by smallufo on 2022-06-21.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named

/**
 * 大命化忌入本命夫妻宫
 */
@Named
class ZRule9 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getTransFourHouseOf(ITransFour.Value.忌, FlowType.SECTION) == sectionPlate.getHouseDataOf(House.夫妻, FlowType.MAIN)
  }
}
