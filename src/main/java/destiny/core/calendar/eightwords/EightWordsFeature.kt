/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
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

class EightWordsFeature : Feature<EightWordsConfig , EightWords>{
  override val key: String = "eightWords"

  override val defaultConfig: EightWordsConfig = EightWordsConfig()

  override val builder: Builder<EightWordsConfig> = EightWordsConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: EightWordsConfig): EightWords {
    TODO("Not yet implemented")
  }
}
