/**
 * Created by smallufo on 2022-06-21.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named

/**
 * 大命化忌入運限夫妻宫
 */
@Named
class ZRule7 : AbstractSeqBooleanRule() {
  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getHouseDataOf(House.命宮)?.let { houseData ->
      val sb = houseData.stemBranch
      logger.trace { "大限命宮 干支 = $sb" }

      sectionPlate.getTransFourHouseOf(T4Value.忌, FlowType.SECTION).house == House.夫妻

//      ITransFour.Value.values().any { value ->
//        val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, value)
//        sectionPlate.getHouseOf(zStar, FlowType.SECTION) == House.夫妻
//      }
    } ?:false
  }
}

/**
 * 本命化忌入運限夫妻宫
 */
@Named
class ZRule8 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getTransFourHouseOf(T4Value.忌, FlowType.MAIN) == sectionPlate.getHouseDataOf(House.夫妻)
  }
}

/**
 * 大命化忌入本命夫妻宫
 */
@Named
class ZRule9 : AbstractSeqBooleanRule() {

  override fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return sectionPlate.getTransFourHouseOf(T4Value.忌, FlowType.SECTION) == sectionPlate.getHouseDataOf(House.夫妻, FlowType.MAIN)
  }
}
