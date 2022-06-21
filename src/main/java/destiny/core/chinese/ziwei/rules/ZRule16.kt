/**
 * Created by smallufo on 2022-06-21.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named


/**
 * 運限夫妻宮有擎羊
 */
@Named
class ZRule16 : AbstractSeqBooleanRule() {
  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.夫妻, FlowType.SECTION)!!.stars.contains(StarUnlucky.擎羊)
  }
}

/**
 * 運限夫妻宮有大羊
 */
//@Named
//class ZRule17 : AbstractSeqBooleanRule() {
//  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
//    return sectionPlate.getHouseDataOf(House.夫妻, FlowType.SECTION)!!.stars.contains(StarUnlucky.擎羊)
//  }
//}

/**
 * 運限夫妻宫有火星
 */
@Named
class ZRule18 : AbstractSeqBooleanRule() {
  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.夫妻, FlowType.SECTION)!!.stars.contains(StarUnlucky.火星)
  }
}
