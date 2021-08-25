/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.YearMonthConfig
import destiny.core.calendar.eightwords.YearMonthFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class MonthlyConfig(val impl: Impl = Impl.AoHead,
                         val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS,
                         val yearlyConfig: YearlyConfig = YearlyConfig(),
                         val yearMonthConfig: YearMonthConfig = YearMonthConfig(),
                         val dayHourConfig: DayHourConfig = DayHourConfig()): java.io.Serializable {
  enum class Impl {
    AoHead,           // 《鰲頭通書》
    AnimalExplained   // 《剋擇講義》
  }
}

@DestinyMarker
class MonthlyConfigBuilder : Builder<MonthlyConfig> {
  var impl: MonthlyConfig.Impl = MonthlyConfig.Impl.AoHead

  override fun build(): MonthlyConfig {
    return MonthlyConfig(impl)
  }

  companion object {
    fun monthly(block: MonthlyConfigBuilder.() -> Unit = {}): MonthlyConfig {
      return MonthlyConfigBuilder().apply(block).build()
    }
  }
}

interface ILunarStationMonthlyFeature : Feature<MonthlyConfig, LunarStation> {

  fun getMonthly(yearStation: LunarStation, monthNumber: Int, impl: MonthlyConfig.Impl): LunarStation
}

class LunarStationMonthlyFeature(private val yearlyFeature: LunarStationYearlyFeature,
                                 private val monthFeature: YearMonthFeature,
                                 private val chineseDateFeature: ChineseDateFeature,
                                 private val lunarStationImplMap: Map<MonthlyConfig.Impl, ILunarStationMonthly>) : ILunarStationMonthlyFeature {
  override val key: String = "lsMonthly"

  override val defaultConfig: MonthlyConfig = MonthlyConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: MonthlyConfig): LunarStation {
    val yearStation = yearlyFeature.getModel(gmtJulDay, loc, config.yearlyConfig).station

    val chineseDate = chineseDateFeature.getModel(gmtJulDay, loc, config.dayHourConfig)
    val monthBranch = monthFeature.getModel(gmtJulDay, loc, config.yearMonthConfig).branch
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, config.monthAlgo
    )

    return lunarStationImplMap[config.impl]!!.getMonthly(yearStation, monthNumber)
  }

  override fun getMonthly(yearStation: LunarStation, monthNumber: Int, impl: MonthlyConfig.Impl): LunarStation {
    return lunarStationImplMap[impl]!!.getMonthly(yearStation, monthNumber)
  }
}
