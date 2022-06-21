/**
 * Created by smallufo on 2022-06-21.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named


/**
 * 5.運限田宅宮干飛化忌入大命、且有桃花星在大命
 */
@Named
class ZRule5 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.田宅, FlowType.SECTION)?.let { houseData ->
      val sb = houseData.stemBranch
      logger.trace { "大限田宅宮 干支 = $sb" }

      val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, ITransFour.Value.忌)
      logger.trace { "${sb.stem} 化忌 = $zStar" }

      val 運限田宅宮干飛化忌入大命 = sectionPlate.getHouseOf(zStar, FlowType.SECTION) == House.命宮
      val 桃花星在大限命宮 = pinkyInHouse(sectionPlate, FlowType.SECTION, House.命宮)

      運限田宅宮干飛化忌入大命 && 桃花星在大限命宮
    } ?: false
  }
}
