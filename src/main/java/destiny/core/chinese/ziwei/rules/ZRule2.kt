/**
 * Created by smallufo on 2022-06-07.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named

/**
 * 運限夫妻宮干飛化忌入本命
 */
@Named
class ZRule2 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.夫妻, FlowType.SECTION)?.let { houseData ->
      val sb = houseData.stemBranch
      logger.trace { "大限夫妻宮 干支 = $sb" }

      val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, T4Value.忌)
      logger.trace { "${sb.stem} 化忌 = $zStar" }

      sectionPlate.getHouseOf(zStar, FlowType.MAIN) == House.命宮
    } ?: false
  }

}
