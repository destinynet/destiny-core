/**
 * Created by smallufo on 2023-06-30.
 */
package destiny.core.chinese.lunarStation

import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.IDayHourConfig
import destiny.core.calendar.eightwords.IEightWordsConfig
import destiny.core.calendar.eightwords.IYearMonthConfig
import destiny.core.chinese.YearType


interface IYearlyConfig  {
  var yearType: YearType

  var yearEpoch: YearEpoch

  val yearlyConfig: YearlyConfig
    get() = YearlyConfig(yearType, yearEpoch)
}


interface IMonthlyConfig : IYearlyConfig , IYearMonthConfig //, IDayHourConfig
{
  var monthlyImpl: MonthlyImpl
  var monthAlgo: MonthAlgo

  val monthlyConfig: MonthlyConfig
    get() = MonthlyConfig(monthlyImpl, monthAlgo, yearlyConfig)
}

interface IHourlyConfig : IDayHourConfig {
  var hourlyImpl: HourlyImpl
  val hourlyConfig: HourlyConfig
    get() = HourlyConfig(hourlyImpl, dayHourConfig)
}

interface ILunarStationConfig : IYearlyConfig, IMonthlyConfig, IHourlyConfig, IEightWordsConfig  {

  val lunarStationConfig: LunarStationConfig
    get() {
      return LunarStationConfig(
        monthlyConfig = monthlyConfig,
        hourlyConfig = hourlyConfig,
      )
    }
}
