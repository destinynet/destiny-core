/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.CacheGrain
import destiny.tools.DestinyMarker
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache


@Serializable
data class EightWordsConfig(
  val yearMonthConfig: YearMonthConfig = YearMonthConfig(),
  val dayHourConfig: DayHourConfig = DayHourConfig()
): java.io.Serializable

@DestinyMarker
class EightWordsConfigBuilder : Builder<EightWordsConfig> {
  var yearMonthConfig: YearMonthConfig = YearMonthConfig()

  fun yearMonth(block: YearMonthConfigBuilder.() -> Unit) {
    this.yearMonthConfig = YearMonthConfigBuilder.yearMonthConfig(block)
  }

  var dayHourConfig: DayHourConfig = DayHourConfig()

  fun dayHour(block: DayHourConfigBuilder.() -> Unit) {
    this.dayHourConfig = DayHourConfigBuilder.dayHour(block)
  }

  override fun build(): EightWordsConfig {
    return EightWordsConfig(yearMonthConfig, dayHourConfig)
  }

  companion object {
    fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}): EightWordsConfig {
      return EightWordsConfigBuilder().apply(block).build()
    }
  }
}

@Named
class EightWordsFeature(private val yearFeature: YearFeature,
                        private val yearMonthFeature: YearMonthFeature,
                        private val dayHourFeature: IDayHourFeature,
                        private val julDayResolver: JulDayResolver,
                        @Transient
                        private val ewFeatureLmtCache : Cache<LmtCacheKey<*> , IEightWords>) : AbstractCachedFeature<EightWordsConfig, IEightWords>() {

  override val key: String = "eightWords"

  override val defaultConfig: EightWordsConfig = EightWordsConfig()

  @Suppress("UNCHECKED_CAST")
  override val lmtCache: Cache<LmtCacheKey<EightWordsConfig>, IEightWords>
    get() = ewFeatureLmtCache as Cache<LmtCacheKey<EightWordsConfig>, IEightWords>

  override var lmtCacheGrain: CacheGrain? = CacheGrain.SECOND

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: EightWordsConfig): IEightWords {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc , julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: EightWordsConfig): IEightWords {
    val year: StemBranch = yearFeature.getModel(lmt, loc, config.yearMonthConfig.yearConfig)
    val month: IStemBranch = yearMonthFeature.getModel(lmt, loc, config.yearMonthConfig)

    val (day, hour) = dayHourFeature.getModel(lmt, loc, config.dayHourConfig)

    return EightWords(year, month, day, hour)
  }

  companion object {
    const val CACHE_EIGHTWORDS_FEATURE_LMT = "ewFeatureLmtCache"
  }
}
