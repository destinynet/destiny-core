/**
 * Created by smallufo on 2023-06-29.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.TransConfig
import java.io.Serializable

interface IYearConfig : Serializable {
  var changeYearDegree: Double

  val yearConfig: YearConfig
    get() = YearConfig(changeYearDegree)
}

interface IMonthConfig : Serializable {
  var southernHemisphereOpposition: Boolean
  var hemisphereBy: HemisphereBy
  var monthImpl: MonthImpl

  val monthConfig: MonthConfig
    get() = MonthConfig(southernHemisphereOpposition, hemisphereBy, monthImpl)
}


interface IYearMonthConfig : IYearConfig, IMonthConfig {
  val yearMonthConfig: YearMonthConfig
    get() = YearMonthConfig(yearConfig, monthConfig)
}

interface IDayConfig : Serializable {
  var changeDayAfterZi: Boolean
  var midnight: MidnightImpl

  val dayConfig: DayConfig
    get() = DayConfig(changeDayAfterZi, midnight)
}

interface ITransConfig : Serializable {
  var discCenter: Boolean
  var refraction: Boolean
  var temperature: Double
  var pressure: Double

  val transConfig: TransConfig
    get() = TransConfig(discCenter, refraction, temperature, pressure)
}


interface IHourBranchConfig : ITransConfig {

  var hourImpl: HourImpl

  val hourBranchConfig: HourBranchConfig
    get() = HourBranchConfig(hourImpl, transConfig)
}


interface IDayHourConfig : IDayConfig, IHourBranchConfig {
  val dayHourConfig: DayHourConfig
    get() = DayHourConfig(dayConfig, hourBranchConfig)
}

interface IEightWordsConfig : IYearMonthConfig, IDayHourConfig {
  val ewConfig: EightWordsConfig
    get() = EightWordsConfig(yearMonthConfig, dayHourConfig)
}
