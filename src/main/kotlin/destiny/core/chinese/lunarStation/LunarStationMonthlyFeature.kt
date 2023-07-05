/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.YearMonthFeature
import destiny.tools.AbstractCachedFeature
import destiny.tools.Feature
import jakarta.inject.Named

enum class MonthlyImpl {
  AoHead,           // 《鰲頭通書》
  AnimalExplained   // 《剋擇講義》
}

interface ILunarStationMonthlyFeature : Feature<ILunarStationConfig, LunarStation> {

  fun getMonthly(yearStation: LunarStation, monthNumber: Int, monthlyImpl: MonthlyImpl): LunarStation
}

@Named
class LunarStationMonthlyFeature(
  private val yearlyFeature: LunarStationYearlyFeature,
  private val monthFeature: YearMonthFeature,
  private val chineseDateFeature: ChineseDateFeature,
  private val lunarStationImplMap: Map<MonthlyImpl, ILunarStationMonthly>
) : ILunarStationMonthlyFeature,
    AbstractCachedFeature<ILunarStationConfig, LunarStation>() {
  override val key: String = "lsMonthly"

  override val defaultConfig: ILunarStationConfig = LunarStationConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: ILunarStationConfig): LunarStation {
    val yearStation = yearlyFeature.getModel(gmtJulDay, loc, config).station

    val chineseDate = chineseDateFeature.getModel(gmtJulDay, loc, config.dayHourConfig)
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
