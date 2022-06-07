/**
 * Created by smallufo on 2022-06-07.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Inject
import javax.inject.Named


/**
 * 桃花星定義: 天相 巨門 太陰 天同 破軍 貪狼 (廉貪對宮的廉貞)
 * 運限子女宮干飛化忌入大命、且有桃花星在大命
 */
@Named
class ZRule3 : ZRule<House?> {

  @Inject
  private lateinit var transFourImplMap: Map<TransFour, ITransFour>

  override fun IZiweiFeature.test(plate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): House? {
    this.getFlowSection(plate, lmt, config)?.let { flowPlate ->
      flowPlate.getHouseDataOf(FlowType.SECTION, House.子女)?.let { houseData ->
        val sb = houseData.stemBranch
        logger.trace { "大限子女宮 干支 = $sb" }

        val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, ITransFour.Value.忌)
        logger.trace { "${sb.stem} 化忌 = $zStar" }

        val sectionHouse = flowPlate.getHouseDataOf(FlowType.SECTION, zStar)
      }
    }
    TODO()
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
