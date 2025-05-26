/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import destiny.tools.*
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.cache.Cache


@Serializable
data class EightWordsConfig(
  override val yearMonthConfig: YearMonthConfig = YearMonthConfig(),
  override val dayHourConfig: DayHourConfig = DayHourConfig()
) : IEightWordsConfig, IYearMonthConfig by yearMonthConfig, IDayHourConfig by dayHourConfig

context(IYearMonthConfig, IDayHourConfig)
@DestinyMarker
class EightWordsConfigBuilder : Builder<EightWordsConfig> {

  override fun build(): EightWordsConfig {
    return EightWordsConfig(yearMonthConfig, dayHourConfig)
  }

  companion object {
    context(IYearMonthConfig, IDayHourConfig)
    fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}): EightWordsConfig {
      return EightWordsConfigBuilder().apply(block).build()
    }
  }
}


interface IEightWordsFeature : Feature<IEightWordsConfig , IEightWords> {

  /**
   * 傳回下一個八字 以及該八字的開始時刻
   */
  fun next(gmtJulDay: GmtJulDay, loc: ILocation, config: IEightWordsConfig): Pair<IEightWords, GmtJulDay>

  /**
   * 傳回上一個八字 以及該八字的開始時刻
   */
  fun prev(gmtJulDay: GmtJulDay, loc: ILocation, config: IEightWordsConfig): Pair<IEightWords, GmtJulDay>
}

@Named
class EightWordsFeature(
  private val yearFeature: YearFeature,
  private val yearMonthFeature: YearMonthFeature,
  private val dayHourFeature: IDayHourFeature,
  private val hourBranchFeature: IHourBranchFeature,
  private val julDayResolver: JulDayResolver,
  @Transient
  private val ewFeatureLmtCache: Cache<LmtCacheKey<*>, IEightWords>
) : AbstractCachedFeature<IEightWordsConfig, IEightWords>(), IEightWordsFeature {

  override val key: String = "eightWords"

  override val defaultConfig: EightWordsConfig = EightWordsConfig()

  @Suppress("UNCHECKED_CAST")
  override val lmtCache: Cache<LmtCacheKey<IEightWordsConfig>, IEightWords>
    get() = ewFeatureLmtCache as Cache<LmtCacheKey<IEightWordsConfig>, IEightWords>

  override var lmtCacheGrain: CacheGrain? = CacheGrain.SECOND

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IEightWordsConfig): IEightWords {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: IEightWordsConfig): IEightWords {
    val year: StemBranch = yearFeature.getModel(lmt, loc, config.yearMonthConfig.yearConfig)
    val month: IStemBranch = yearMonthFeature.getModel(lmt, loc, config.yearMonthConfig)

    val (day, hour) = dayHourFeature.getModel(lmt, loc, config.dayHourConfig)

    return EightWords(year, month, day, hour)
  }

  override fun next(gmtJulDay: GmtJulDay, loc: ILocation, config: IEightWordsConfig): Pair<IEightWords, GmtJulDay> {
    val currentEw = getModel(gmtJulDay, loc, config)
    val nextHourBranch = currentEw.hour.next.branch
    val nextHourStartGmt = hourBranchFeature.getGmtNextStartOf(gmtJulDay, loc, nextHourBranch, julDayResolver, config.hourBranchConfig)

    val delta = 0.01
    return (getModel(nextHourStartGmt + delta, loc, config) to nextHourStartGmt)
  }

  override fun prev(gmtJulDay: GmtJulDay, loc: ILocation, config: IEightWordsConfig): Pair<IEightWords, GmtJulDay> {
    val currentEw = getModel(gmtJulDay, loc, config)
    val prevHourBranch = currentEw.hour.prev.branch
    val prevHourStartGmt = hourBranchFeature.getGmtPrevStartOf(gmtJulDay, loc, prevHourBranch, julDayResolver, config.hourBranchConfig)
    val delta = 0.01
    return getModel(prevHourStartGmt + delta, loc, config) to prevHourStartGmt
  }

  companion object {
    const val CACHE_EIGHTWORDS_FEATURE_LMT = "ewFeatureLmtCache"
  }
}
