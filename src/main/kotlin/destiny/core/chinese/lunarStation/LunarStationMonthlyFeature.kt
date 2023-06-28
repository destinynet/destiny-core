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
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.YearMonthFeature
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
data class MonthlyConfig(val impl: MonthlyImpl = MonthlyImpl.AoHead,
                         val monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS,
                         val yearlyConfig: YearlyConfig = YearlyConfig(),
                         val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable
@DestinyMarker
class MonthlyConfigBuilder : Builder<MonthlyConfig> {

  var impl: MonthlyImpl = MonthlyImpl.AoHead

  var monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS

  var yearlyConfig: YearlyConfig = YearlyConfig()

  fun yearly(block: YearlyConfigBuilder.() -> Unit = {}) {
    this.yearlyConfig = YearlyConfigBuilder.yearly(block)
  }

  var ewConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}) {
    this.ewConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  override fun build(): MonthlyConfig {
    return MonthlyConfig(impl, monthAlgo, yearlyConfig, ewConfig)
  }

  companion object {
    fun monthly(block: MonthlyConfigBuilder.() -> Unit = {}): MonthlyConfig {
      return MonthlyConfigBuilder().apply(block).build()
    }
  }
}

interface ILunarStationMonthlyFeature : Feature<MonthlyConfig, LunarStation> {

  fun getMonthly(yearStation: LunarStation, monthNumber: Int, monthlyImpl: MonthlyImpl): LunarStation
}

@Named
class LunarStationMonthlyFeature(private val yearlyFeature: LunarStationYearlyFeature,
                                 private val monthFeature: YearMonthFeature,
                                 private val chineseDateFeature: ChineseDateFeature,
                                 private val lunarStationImplMap: Map<MonthlyImpl, ILunarStationMonthly>) : ILunarStationMonthlyFeature,
                                                                                                            AbstractCachedFeature<MonthlyConfig, LunarStation>() {
  override val key: String = "lsMonthly"

  override val defaultConfig: MonthlyConfig = MonthlyConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: MonthlyConfig): LunarStation {
    val yearStation = yearlyFeature.getModel(gmtJulDay, loc, config.yearlyConfig).station

    val chineseDate = chineseDateFeature.getModel(gmtJulDay, loc, config.eightWordsConfig.dayHourConfig)
    val monthBranch = monthFeature.getModel(gmtJulDay, loc, config.eightWordsConfig.yearMonthConfig).branch
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, config.monthAlgo
    )

    return lunarStationImplMap[config.impl]!!.getMonthly(yearStation, monthNumber)
  }

  override fun getMonthly(yearStation: LunarStation, monthNumber: Int, monthlyImpl: MonthlyImpl): LunarStation {
    return lunarStationImplMap[monthlyImpl]!!.getMonthly(yearStation, monthNumber)
  }
}
