/**
 * Created by smallufo on 2022-06-09.
 */
package destiny.core.chinese.ziwei.rules

import destiny.core.chinese.ziwei.*
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Inject


abstract class AbstractSeqBooleanRule : ZRule<Boolean> {

  @Inject
  protected lateinit var transFourImplMap: Map<TransFour, ITransFour>

  private val pinkyStars = setOf(StarMain.天相, StarMain.巨門, StarMain.太陰, StarMain.天同, StarMain.破軍, StarMain.貪狼)

  val 鸞喜 = setOf(StarMinor.紅鸞, StarMinor.天喜)

  override fun IZiweiFeature.test(plate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean {
    return this.getFlowSections(plate , lmt, config).drop(1).any { sectionPlate ->
      testSection(sectionPlate, lmt, config)
    }
  }

  abstract fun testSection(sectionPlate: IPlate, lmt: ChronoLocalDateTime<*>, config: ZiweiConfig): Boolean

  protected fun pinkyInHouse(plate: IPlate, flowType: FlowType, house: House): Boolean {
    return plate.getHouseDataOf(house, flowType)?.let { houseData ->
      val basic1: Boolean = houseData.stars.any { it in pinkyStars }

      val basic2 = if (houseData.stars.contains(StarMain.廉貞)) {
        // 廉貞 在此 house
        val oppoBranch = houseData.stemBranch.branch.opposite
        plate.getHouseDataOf(oppoBranch).stars.contains(StarMain.貪狼)
      } else
        false

      basic1 || basic2
    } ?: false
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
