/**
 * Created by smallufo on 2021-08-20.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.lunarStation.HourlyConfigBuilder.Companion.hourly
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HourlyConfigTest : AbstractConfigTest<HourlyConfig>() {
  override val serializer: KSerializer<HourlyConfig> = HourlyConfig.serializer()

  override val configByConstructor: HourlyConfig = HourlyConfig(
    HourlyImpl.Fixed,
    DayHourConfig(
      DayConfig(changeDayAfterZi = false, midnight = MidnightImpl.CLOCK0),
      HourBranchConfig(hourImpl = HourImpl.LMT)
    )
  )

  override val configByFunction: HourlyConfig
    get() {

      return with(DayHourConfig()) {
        hourly {
          impl = HourlyImpl.Fixed

          changeDayAfterZi = false
          midnight = MidnightImpl.CLOCK0
          hourImpl = HourImpl.LMT
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""hourlyImpl":\s*"Fixed"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
  }
}
