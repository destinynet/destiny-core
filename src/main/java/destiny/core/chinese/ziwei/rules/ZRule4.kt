/**
 * Created by smallufo on 2022-06-10.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named


/**
 * 桃花星定義: 天相 巨門 太陰 天同 破軍 貪狼 (廉貪對宮的廉貞)
 * 運限子女宮干飛化忌入本命、且有桃花星在大命
 */
@Named
class ZRule4 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.子女, FlowType.SECTION)?.let { houseData ->
      val sb = houseData.stemBranch
      logger.trace { "大限子女宮 干支 = $sb" }

      val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, T4Value.忌)
      logger.trace { "${sb.stem} 化忌 = $zStar" }

      val 運限子女宮干飛化忌入本命 = sectionPlate.getHouseOf(zStar, FlowType.MAIN) == House.命宮
      val 桃花星在大限命宮 = pinkyInHouse(sectionPlate, FlowType.SECTION, House.命宮)

      運限子女宮干飛化忌入本命 && 桃花星在大限命宮
    } ?: false
  }
}
