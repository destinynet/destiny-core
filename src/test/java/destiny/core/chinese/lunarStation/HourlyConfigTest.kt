/**
 * Created by smallufo on 2021-08-20.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.DayConfig
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.calendar.eightwords.HourImpl
import destiny.core.chinese.lunarStation.HourlyConfigBuilder.Companion.hourly
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HourlyConfigTest : AbstractConfigTest<HourlyConfig>() {
  override val serializer: KSerializer<HourlyConfig> = HourlyConfig.serializer()

  override val configByConstructor: HourlyConfig = HourlyConfig(
    HourlyImpl.Fixed,
    DayHourConfig(
      DayConfig(changeDayAfterZi = false, midnight = DayConfig.MidnightImpl.CLOCK0),
      HourBranchConfig(hourImpl = HourImpl.LMT)
    )
  )

  override val configByFunction: HourlyConfig = hourly {
    impl = HourlyImpl.Fixed
    dayHour {
      day {
        changeDayAfterZi = false
        midnight = DayConfig.MidnightImpl.CLOCK0
      }
      hourBranch {
        hourImpl = HourImpl.LMT
      }
    }
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"Fixed"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
  }
}
