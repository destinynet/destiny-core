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
 * 運限夫妻宮干飛化忌入大限的 x宮
 */
@Named
class ZRule1 : ZRule<House?> {

  @Inject
  private lateinit var transFourImplMap: Map<TransFour, ITransFour>

  override fun IZiweiFeature.test(plate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): House? {
    return this.getFlowSection(plate, lmt, config)?.let { flowPlate ->
      flowPlate.getHouseDataOf(FlowType.SECTION, House.夫妻)?.let { houseData ->
        val sb = houseData.stemBranch
        logger.trace { "大限夫妻宮 干支 = $sb" }

        val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, ITransFour.Value.忌)
        logger.trace { "${sb.stem} 化忌 = $zStar" }
        flowPlate.getHouseDataOf(FlowType.SECTION, zStar)
      }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
