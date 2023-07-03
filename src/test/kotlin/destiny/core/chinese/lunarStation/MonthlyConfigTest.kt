/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.DayConfigBuilder.Companion.dayConfig
import destiny.core.calendar.eightwords.DayHourConfigBuilder.Companion.dayHour
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.HourBranchConfigBuilder.Companion.hourBranchConfig
import destiny.core.calendar.eightwords.HourImpl
import destiny.core.calendar.eightwords.MidnightImpl.CLOCK0
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.MonthlyConfigBuilder.Companion.monthly
import destiny.core.chinese.lunarStation.YearlyConfigBuilder.Companion.yearly
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MonthlyConfigTest : AbstractConfigTest<MonthlyConfig>() {
  override val serializer: KSerializer<MonthlyConfig> = MonthlyConfig.serializer()

  override val configByConstructor: MonthlyConfig = MonthlyConfig(
    MonthlyImpl.AnimalExplained,
    MonthAlgo.MONTH_LEAP_SPLIT15,
    yearlyConfig = YearlyConfig(yearType = YearType.YEAR_LUNAR)
  )

  override val configByFunction: MonthlyConfig
    get() {

      val transConfig = trans {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0
      }

      val hourBranchConfig = with(transConfig) {
        hourBranchConfig {
          hourImpl = HourImpl.TST
        }
      }


      val dayConfig = dayConfig {
        changeDayAfterZi = false
        midnight = CLOCK0
      }

      // FIXME MonthlyConfig 沒有實作 IDayHourConfig , 這無法傳入
      val dayHourConfig = with(hourBranchConfig) {
        with(dayConfig) {
          dayHour {  }
        }
      }

      val yearlyConfig = with(dayHourConfig) {
        yearly {
          yearType = YearType.YEAR_LUNAR
        }
      }



      return with(yearlyConfig) {
        with(EightWordsConfig()) {
          monthly {
            impl = MonthlyImpl.AnimalExplained
            monthAlgo = MonthAlgo.MONTH_LEAP_SPLIT15
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
