/**
 * Created by smallufo on 2022-06-21.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.FlowType
import destiny.core.chinese.ziwei.House
import destiny.core.chinese.ziwei.IPlate
import destiny.core.chinese.ziwei.ZiweiConfig
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named


/**
 * 運限命宫疊上本命夫妻宫
 */
@Named
class ZRule13 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.命宮, FlowType.SECTION)!!.stemBranch ==
      sectionPlate.getHouseDataOf(House.夫妻, FlowType.MAIN)!!.stemBranch
  }
}

/**
 * 運限命宫疊上本命子女宫
 */
@Named
class ZRule14 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.命宮, FlowType.SECTION)!!.stemBranch ==
      sectionPlate.getHouseDataOf(House.子女, FlowType.MAIN)!!.stemBranch
  }
}

/**
 * 運限命宫疊上本命田宅宫
 */
@Named
class ZRule15 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.命宮, FlowType.SECTION)!!.stemBranch ==
      sectionPlate.getHouseDataOf(House.田宅, FlowType.MAIN)!!.stemBranch
  }
}
