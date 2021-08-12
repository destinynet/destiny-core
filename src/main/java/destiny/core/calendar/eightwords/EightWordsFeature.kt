/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
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

@DestinyMarker
class EightWordsConfigBuilder(private val monthConfigBuilder : MonthConfigBuilder = MonthConfigBuilder(),
                              private val hourConfigBuilder: HourConfigBuilder = HourConfigBuilder()) : Builder<EightWordsConfig>
{
  private var monthConfig: MonthConfig = MonthConfig()

  fun monthConfig(block: MonthConfigBuilder.() -> Unit) {
    this.monthConfig = monthConfigBuilder.apply(block).build()
  }

  private var hourConfig: HourConfig = HourConfig()

  fun hourConfig(block: HourConfigBuilder.() -> Unit) {
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
                        private val dayHourFeature: DayHourFeature) : Feature<EightWordsConfig , EightWords> {
  override val key: String = "eightWords"

  override val defaultConfig: EightWordsConfig = EightWordsConfig()

  override val builder: Builder<EightWordsConfig> = EightWordsConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: EightWordsConfig): EightWords {
    val year: StemBranch = yearFeature.getModel(gmtJulDay, loc, config.monthConfig.yearConfig)
    val month: IStemBranch = monthFeature.getModel(gmtJulDay, loc, config.monthConfig)

    val (day, hour) = dayHourFeature.getModel(gmtJulDay, loc)

    return EightWords(year, month, day, hour)
  }
}
