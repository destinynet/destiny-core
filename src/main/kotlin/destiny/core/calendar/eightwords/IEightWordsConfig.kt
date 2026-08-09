/**
 * Created by smallufo on 2023-06-29.
 */
package destiny.core.calendar.eightwords

import destiny.core.IPresentConfig
import destiny.core.astrology.DayNightConfig
import destiny.core.astrology.DayNightImpl
import destiny.core.astrology.StarTypeOptions
import destiny.core.astrology.TransConfig
import destiny.core.astrology.ZodiacSignConfig
import destiny.core.chinese.eightwords.*
import destiny.tools.JSerializable

interface IYearConfig : JSerializable {
  var changeYearDegree: Double

  val yearConfig: YearConfig
    get() = YearConfig(changeYearDegree)
}

interface IMonthConfig : JSerializable {
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

interface IDayConfig : JSerializable {
  var changeDayAfterZi: Boolean
  var midnight: MidnightImpl

  val dayConfig: DayConfig
    get() = DayConfig(changeDayAfterZi, midnight)
}

interface ITransConfig : JSerializable {
  var discCenter: Boolean
  var refraction: Boolean
  var temperature: Double
  var pressure: Double
  var starTypeOptions: StarTypeOptions

  val transConfig: TransConfig
    get() = TransConfig(discCenter, refraction, temperature, pressure, starTypeOptions)
}

interface IDayNightConfig : ITransConfig {
  val dayNightImpl: DayNightImpl

  val dayNightConfig
    get() = DayNightConfig(dayNightImpl, transConfig)
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

interface IEightWordsContextConfig : IEightWordsConfig , IRisingSignConfig {
  var zodiacSignConfig: ZodiacSignConfig

  val ewContextConfig: EightWordsContextConfig
    get() = EightWordsContextConfig(ewConfig, risingSignConfig, zodiacSignConfig)
}


interface IEightWordsPersonConfig : IEightWordsContextConfig {
  var fortuneLargeConfig: FortuneLargeConfig
  var fortuneSmallConfig: FortuneSmallConfig
  var ewContextScore: EwContextScore

  val ewPersonConfig: EightWordsPersonConfig
    get() = EightWordsPersonConfig(ewContextConfig, fortuneLargeConfig, fortuneSmallConfig, ewContextScore)
}

interface IPersonPresentConfig : IEightWordsPersonConfig , IPresentConfig {
  val personPresentConfig: PersonPresentConfig
    get() = PersonPresentConfig(ewPersonConfig, viewGmt)
}
