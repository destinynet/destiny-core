/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.*
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import jakarta.inject.Named
import kotlinx.serialization.Serializable

enum class MonthlyImpl {
  AoHead,           // 《鰲頭通書》
  AnimalExplained   // 《剋擇講義》
}

@Serializable
data class MonthlyConfig(
  override var monthlyImpl: MonthlyImpl = MonthlyImpl.AoHead,
  override var monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS,
  override val yearlyConfig: YearlyConfig = YearlyConfig(),
  override val yearMonthConfig: YearMonthConfig = YearMonthConfig(),
  override val dayHourConfig: DayHourConfig = DayHourConfig()
) : IMonthlyConfig,
    IYearlyConfig by yearlyConfig,
    IYearMonthConfig by yearMonthConfig,
    IDayHourConfig by dayHourConfig


context(IYearlyConfig, IYearMonthConfig)
@DestinyMarker
class MonthlyConfigBuilder : Builder<MonthlyConfig> {

  var impl: MonthlyImpl = MonthlyImpl.AoHead

  var monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS

  override fun build(): MonthlyConfig {
    return MonthlyConfig(impl, monthAlgo, yearlyConfig, yearMonthConfig)
  }

  companion object {
    context(IYearlyConfig , IYearMonthConfig)
    fun monthly(block: MonthlyConfigBuilder.() -> Unit = {}): MonthlyConfig {
      return MonthlyConfigBuilder().apply(block).build()
    }
  }
}

interface ILunarStationMonthlyFeature : Feature<IMonthlyConfig, LunarStation> {

  fun getMonthly(yearStation: LunarStation, monthNumber: Int, monthlyImpl: MonthlyImpl): LunarStation
}

@Named
class LunarStationMonthlyFeature(
  private val yearlyFeature: LunarStationYearlyFeature,
  private val monthFeature: YearMonthFeature,
  private val chineseDateFeature: ChineseDateFeature,
  private val lunarStationImplMap: Map<MonthlyImpl, ILunarStationMonthly>
) : ILunarStationMonthlyFeature,
    AbstractCachedFeature<IMonthlyConfig, LunarStation>() {
  override val key: String = "lsMonthly"

  override val defaultConfig: MonthlyConfig = MonthlyConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: IMonthlyConfig): LunarStation {
    val yearStation = yearlyFeature.getModel(gmtJulDay, loc, config.yearlyConfig).station

    val dayHourConfig = (config as MonthlyConfig).dayHourConfig

    val chineseDate = chineseDateFeature.getModel(gmtJulDay, loc, dayHourConfig)
    val monthBranch = monthFeature.getModel(gmtJulDay, loc, config).branch
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, config.monthAlgo
    )

    return lunarStationImplMap[config.monthlyImpl]!!.getMonthly(yearStation, monthNumber)
  }

  override fun getMonthly(yearStation: LunarStation, monthNumber: Int, monthlyImpl: MonthlyImpl): LunarStation {
    return lunarStationImplMap[monthlyImpl]!!.getMonthly(yearStation, monthNumber)
  }
}
