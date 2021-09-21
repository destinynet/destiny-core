/**
 * Created by smallufo on 2021-09-21.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.astrology.DayNightConfig
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.chinese.MonthMaster
import destiny.core.chinese.Tianyi
import destiny.core.chinese.liuren.Clockwise
import destiny.core.chinese.liuren.GeneralSeq
import destiny.core.chinese.liuren.GeneralStemBranch
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import kotlinx.serialization.Serializable

@Serializable
data class PithyConfig(val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
                       val monthMaster : MonthMaster = MonthMaster.StarPosition,
                       val clockwise: Clockwise = Clockwise.XinRenKuiReverse,
                       val dayNightConfig : DayNightConfig = DayNightConfig(impl = DayNightConfig.DayNightImpl.StarPos),
                       val tianyi: Tianyi = Tianyi.LiurenPithy,
                       val generalSeq : GeneralSeq = GeneralSeq.Default,
                       val generalStemBranch : GeneralStemBranch = GeneralStemBranch.Pithy
                       ) : java.io.Serializable

class PithyConfigBuilder : Builder<PithyConfig> {

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block : EightWordsConfigBuilder.() -> Unit = {}) {
    this.eightWordsConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  var monthMaster : MonthMaster = MonthMaster.StarPosition

  var clockwise: Clockwise = Clockwise.XinRenKuiReverse

  var dayNightConfig : DayNightConfig = DayNightConfig(impl = DayNightConfig.DayNightImpl.StarPos)

  var tianyi: Tianyi = Tianyi.LiurenPithy

  var generalSeq : GeneralSeq = GeneralSeq.Default

  var generalStemBranch : GeneralStemBranch = GeneralStemBranch.Pithy

  override fun build(): PithyConfig {
    return PithyConfig(eightWordsConfig, monthMaster, clockwise, dayNightConfig, tianyi, generalSeq, generalStemBranch)
  }

  companion object {
    fun pithyConfig(block : PithyConfigBuilder.() -> Unit = {}) : PithyConfig {
      return PithyConfigBuilder().apply(block).build()
    }
  }
}



class PithyFeature : AbstractCachedFeature<PithyConfig, IPithyModel>() {
  override val key: String = "pithyFeature"

  override val defaultConfig: PithyConfig = PithyConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: PithyConfig): IPithyModel {
    TODO("Not yet implemented")
  }

}
