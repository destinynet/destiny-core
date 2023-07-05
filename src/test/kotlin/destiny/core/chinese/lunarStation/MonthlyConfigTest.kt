/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.MidnightImpl.CLOCK0
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

      val yearlyConfig: YearlyConfig = yearly {
        yearType = YearType.YEAR_LUNAR
        yearEpoch = YearEpoch.EPOCH_1864
      }

      return with(EightWordsConfig()) {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0

        changeDayAfterZi = false
        midnight = CLOCK0


        with(yearlyConfig) {
          monthly {
            impl = MonthlyImpl.AnimalExplained
            monthAlgo = MonthAlgo.MONTH_LEAP_SPLIT15
          }
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertFalse(raw.contains(""""yearType":\s*"YEAR_SOLAR"""".toRegex()))

    assertTrue(raw.contains(""""yearEpoch":\s*"EPOCH_1864"""".toRegex()))
    assertFalse(raw.contains(""""yearEpoch":\s*"EPOCH_1564"""".toRegex()))

    assertTrue(raw.contains(""""monthlyImpl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_LEAP_SPLIT15"""".toRegex()))

    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))

    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))

    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
