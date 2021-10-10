/**
 * Created by smallufo on 2021-09-21.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.DayNight
import destiny.core.astrology.DayNightConfig
import destiny.core.astrology.DayNightFeature
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.*
import destiny.core.chinese.liuren.*
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import javax.inject.Named

@Serializable
data class PithyConfig(val direction: Branch = Branch.子,
                       val eightWordsConfig: EightWordsConfig = EightWordsConfig(),
                       val monthMaster : MonthMaster = MonthMaster.StarPosition,
                       val clockwise: Clockwise = Clockwise.XinRenKuiReverse,
                       val dayNightConfig : DayNightConfig = DayNightConfig(impl = DayNightConfig.DayNightImpl.StarPos),
                       val tianyi: Tianyi = Tianyi.LiurenPithy,
                       val generalSeq : GeneralSeq = GeneralSeq.Default,
                       val generalStemBranch : GeneralStemBranch = GeneralStemBranch.Pithy
                       ) : java.io.Serializable

@DestinyMarker
class PithyConfigBuilder : Builder<PithyConfig> {

  var direction: Branch = Branch.子

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
    return PithyConfig(direction, eightWordsConfig, monthMaster, clockwise, dayNightConfig, tianyi, generalSeq, generalStemBranch)
  }

  companion object {
    fun pithyConfig(block : PithyConfigBuilder.() -> Unit = {}) : PithyConfig {
      return PithyConfigBuilder().apply(block).build()
    }
  }
}


@Named
class PithyCoreFeature(private val eightWordsFeature: EightWordsFeature,
                       private val monthMasterMap: Map<MonthMaster, IMonthMaster>,
                       private val clockwiseMap: Map<Clockwise, IClockwise>,
                       private val dayNightFeature: DayNightFeature,
                       private val tianyiImplMap: Map<Tianyi, ITianyi>,
                       private val generalSeqMap: Map<GeneralSeq, IGeneralSeq>,
                       private val generalStemBranchMap: Map<GeneralStemBranch, IGeneralStemBranch>) : AbstractCachedFeature<PithyConfig, IPithyModel>() {

  override val key: String = "pithyCoreFeature"

  override val defaultConfig: PithyConfig = PithyConfig()

  private fun core(config: PithyConfig, ew: IEightWords, moonMaster: Branch, dayNight: DayNight, clockwise: destiny.core.Clockwise): Pithy {
    // 天乙貴人(起點)
    val tianYi = tianyiImplMap[config.tianyi]!!.getFirstTianyi(ew.day.stem, dayNight)

    val steps = when (clockwise) {
      destiny.core.Clockwise.CLOCKWISE -> config.direction.getAheadOf(tianYi)
      destiny.core.Clockwise.COUNTER   -> tianYi.getAheadOf(config.direction)
    }

    logger.trace("天乙貴人 (日干 {} + {} ) = {} . 地分 = {} , 順逆 = {} , steps = {}", ew.day.stem, dayNight, tianYi, config.direction, clockwise, steps)

    val generalSeqImpl = generalSeqMap[config.generalSeq]!!

    val generalStemBranchImpl = generalStemBranchMap[config.generalStemBranch]!!

    // 貴神地支
    val kueiBranch = General.貴人.next(steps, generalSeqImpl).getStemBranch(generalStemBranchImpl).branch
    // 貴神天干
    val kueiStem = StemBranchUtils.getHourStem(ew.day.stem, kueiBranch)
    logger.trace("推導貴神，從 {} 開始走 {} 步，得到 {} , 地支為 {} , 天干為 {}", General.貴人, steps, General.貴人.next(steps, generalSeqImpl), kueiBranch, kueiStem)
    // 貴神
    val kuei = StemBranch[kueiStem, kueiBranch]

    return Pithy(ew, config.direction, moonMaster, dayNight, kuei)
  }

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: PithyConfig): IPithyModel {
    val ew = eightWordsFeature.getModel(gmtJulDay, loc)

    // 月將
    val moonMaster = monthMasterMap[config.monthMaster]!!.getBranch(gmtJulDay, loc)

    val clockwise = clockwiseMap[config.clockwise]!!.getClockwise(gmtJulDay, loc)
    val dayNight = dayNightFeature.getModel(gmtJulDay, loc, config.dayNightConfig)

    return core(config, ew, moonMaster, dayNight, clockwise)
  }

  companion object {
    private val logger = KotlinLogging.logger {  }
  }

}
