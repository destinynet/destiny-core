/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable


@Serializable
data class EightWordsConfig(
  val monthConfig: MonthConfig = MonthConfig(),
  val hourConfig: HourConfig = HourConfig()
)

interface IEightWordsBuilder : IMonthConfigBuilder , IHourConfigBuilder {
  fun monthConfig(block: IMonthConfigBuilder.() -> Unit = {})
  fun hourConfig(block: HourConfigBuilder.() -> Unit = {})
}

@DestinyMarker
class EightWordsConfigBuilder(private val monthConfigBuilder : MonthConfigBuilder = MonthConfigBuilder(),
                              private val hourConfigBuilder: HourConfigBuilder = HourConfigBuilder()) : Builder<EightWordsConfig> ,IEightWordsBuilder,
                                                                                                        IMonthConfigBuilder by monthConfigBuilder ,
                                                                                                        IHourConfigBuilder by hourConfigBuilder
{
  var monthConfig: MonthConfig = MonthConfig()

  override fun monthConfig(block: IMonthConfigBuilder.() -> Unit) {
    this.monthConfig = monthConfigBuilder.apply(block).build()
  }

  var hourConfig: HourConfig = HourConfig()

  override fun hourConfig(block: HourConfigBuilder.() -> Unit) {
    this.hourConfig = hourConfigBuilder.apply(block).build()
  }

  override fun build(): EightWordsConfig {
    return EightWordsConfig(monthConfig, hourConfig)
  }

  companion object {
    fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}): EightWordsConfig {
      return EightWordsConfigBuilder().apply(block).build()
    }
  }
}

class EightWordsFeature(private val yearFeature: YearFeature,
                        private val monthFeature: MonthFeature ,
                        private val dayFeature : DayFeature,
                        private val hourFeature: HourFeature ,
                        // FIXME : remove dayHourImpl
                        private val dayHourImpl : IDayHour,
                        private val julDayResolver: JulDayResolver) : Feature<EightWordsConfig , EightWords> {
  override val key: String = "eightWords"

  override val defaultConfig: EightWordsConfig = EightWordsConfig()

  override val builder: Builder<EightWordsConfig> = EightWordsConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: EightWordsConfig): EightWords {
    val year: StemBranch = yearFeature.getModel(gmtJulDay, loc, config.monthConfig.yearConfig)
    val month: IStemBranch = monthFeature.getModel(gmtJulDay, loc, config.monthConfig)
    val day: StemBranch = dayFeature.getModel(gmtJulDay, loc, config.hourConfig.dayConfig)
    val hourBranch: Branch = hourFeature.getModel(gmtJulDay, loc, config.hourConfig)

    return getEightWordsByGmt(gmtJulDay, loc, year, month, day, hourBranch, dayHourImpl, config.hourConfig.dayConfig.changeDayAfterZi, julDayResolver)
  }
}
