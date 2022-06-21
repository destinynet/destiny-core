/**
 * Created by smallufo on 2022-06-21.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named


/**
 * 本命化忌入運限夫妻宫
 */
@Named
class ZRule8 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getTransFourHouseOf(ITransFour.Value.忌, FlowType.MAIN) == sectionPlate.getHouseDataOf(House.夫妻)
  }
}
