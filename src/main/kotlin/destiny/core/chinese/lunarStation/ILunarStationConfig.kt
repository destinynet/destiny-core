/**
 * Created by smallufo on 2023-06-30.
 */
package destiny.core.chinese.lunarStation

import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.IEightWordsConfig
import destiny.core.chinese.YearType

interface ILunarStationConfig : IEightWordsConfig {
  var yearType: YearType
  var yearEpoch: YearEpoch
  var monthlyImpl: MonthlyImpl
  var monthAlgo: MonthAlgo
  var hourlyImpl: HourlyImpl

  val lunarStationConfig
    get() = LunarStationConfig(yearType, yearEpoch, monthlyImpl, monthAlgo, hourlyImpl, ewConfig)
}
