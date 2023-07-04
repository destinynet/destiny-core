/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.DayHourConfigBuilder.Companion.dayHour
import destiny.core.calendar.eightwords.MidnightImpl.CLOCK0
import destiny.core.calendar.eightwords.YearMonthConfig
import destiny.core.calendar.eightwords.YearMonthConfigBuilder.Companion.yearMonthConfig
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.MonthlyConfigBuilder.Companion.monthly
import destiny.core.chinese.lunarStation.YearlyConfigBuilder.Companion.yearly
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MonthlyConfigTest : AbstractConfigTest<MonthlyConfig>() {
  override val serializer: KSerializer<MonthlyConfig> = MonthlyConfig.serializer()

  override val configByConstructor: MonthlyConfig? = null


  override val configByFunction: MonthlyConfig
    get() {

      val yearMonthConfig: YearMonthConfig = with(YearMonthConfig()) {
        yearMonthConfig {
        }
      }

      val dayHourConfig: DayHourConfig = with(DayHourConfig()) {
        dayHour {
          discCenter = true
          refraction = false
          temperature = 23.0
          pressure = 1000.0

          changeDayAfterZi = false
          midnight = CLOCK0
        }
      }


      val yearlyConfig: YearlyConfig =
        with(dayHourConfig) {
          yearly {
            yearType = YearType.YEAR_LUNAR
          }
        }


      return with(yearMonthConfig) {
        with(dayHourConfig) {
          with(yearlyConfig) {
            monthly {
              impl = MonthlyImpl.AnimalExplained
              monthAlgo = MonthAlgo.MONTH_LEAP_SPLIT15
            }
          }
        }
      }

    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""monthlyImpl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_LEAP_SPLIT15"""".toRegex()))

    // FIXME : failed
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))

    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))

    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
