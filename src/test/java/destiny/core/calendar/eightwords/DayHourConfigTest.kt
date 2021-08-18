/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.DayHourConfigBuilder.Companion.dayHour
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DayHourConfigTest : AbstractConfigTest<DayHourConfig>() {

  override val serializer: KSerializer<DayHourConfig> = DayHourConfig.serializer()

  override val configByConstructor: DayHourConfig = DayHourConfig(
    DayConfig(changeDayAfterZi = false , midnight = DayConfig.MidnightImpl.CLOCK0),
    HourBranchConfig(HourBranchConfig.HourImpl.LMT)
  )

  override val configByFunction = dayHour {
    day {
      changeDayAfterZi = false
      midnight = DayConfig.MidnightImpl.CLOCK0
    }
    hourBranch {
      hourImpl = HourBranchConfig.HourImpl.LMT
    }
  }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
  }
}
