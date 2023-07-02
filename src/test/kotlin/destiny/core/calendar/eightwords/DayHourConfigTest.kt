/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfig
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import destiny.core.calendar.eightwords.DayConfigBuilder.Companion.dayConfig
import destiny.core.calendar.eightwords.DayHourConfigBuilder.Companion.dayHour
import destiny.core.calendar.eightwords.HourBranchConfigBuilder.Companion.hourBranchConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DayHourConfigTest : AbstractConfigTest<DayHourConfig>() {

  override val serializer: KSerializer<DayHourConfig> = DayHourConfig.serializer()

  override val configByConstructor: DayHourConfig = DayHourConfig(
    DayConfig(changeDayAfterZi = false, midnight = MidnightImpl.CLOCK0),
    HourBranchConfig(
      hourImpl = HourImpl.LMT,
      transConfig = TransConfig(
        discCenter = true,
        refraction = false,
        temperature = 23.0,
        pressure = 1000.0
      )
    )
  )

  override val configByFunction: DayHourConfig
    get() {

      val transConfig = trans {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0
      }

      val hourBranchConfig = with(transConfig) {
        hourBranchConfig {
          hourImpl = HourImpl.LMT
        }
      }

      val dayConfig = dayConfig {
        changeDayAfterZi = false
        midnight = MidnightImpl.CLOCK0
      }

      return with(dayConfig) {
        with(hourBranchConfig) {
          dayHour {

          }
        }
      }

    }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertFalse(raw.contains(""""changeDayAfterZi":\s*true""".toRegex()))

    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))

    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))

    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))

    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
