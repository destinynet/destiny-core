/**
 * Created by smallufo on 2022-06-07.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import destiny.core.chinese.ziwei.StarMain.*
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Inject
import javax.inject.Named


/**
 * 桃花星定義: 天相 巨門 太陰 天同 破軍 貪狼 (廉貪對宮的廉貞)
 * 運限子女宮干飛化忌入大命、且有桃花星在大限X位
 */
@Named
class ZRule3 : ZRule<House?> {

  private val pinkyStars = setOf(天相, 巨門, 太陰, 天同, 破軍, 貪狼)

  @Inject
  private lateinit var transFourImplMap: Map<TransFour, ITransFour>

  override fun IZiweiFeature.test(plate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): House? {
    return this.getFlowSection(plate, lmt, config)?.let { flowPlate ->
      flowPlate.getHouseDataOf(FlowType.SECTION, House.子女)?.let { houseData ->
        val sb = houseData.stemBranch
        logger.trace { "大限子女宮 干支 = $sb" }

        val zStar = transFourImplMap[config.transFour]!!.getStarOf(sb.stem, ITransFour.Value.忌)
        logger.trace { "${sb.stem} 化忌 = $zStar" }


        flowPlate.getHouseDataOf(FlowType.SECTION, zStar)?.takeIf { house: House ->
          // 運限子女宮干飛化忌入此 house

          // 且有桃花星在此 house
          pinkyInHouse(plate, FlowType.SECTION, house)
        }
      }
    }
  }

  private fun pinkyInHouse(plate: IPlate, flowType: FlowType, house: House): Boolean {
    return plate.getHouseDataOf(flowType, house)?.let { houseData ->
      val basic1: Boolean = houseData.stars.any { it in pinkyStars }

      val basic2 = if (houseData.stars.contains(廉貞)) {
        // 廉貞 在此 house
        val oppoBranch = houseData.stemBranch.branch.opposite
        plate.getHouseDataOf(oppoBranch).stars.contains(貪狼)
      } else
        false

      basic1 || basic2
    } ?: false
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
