/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.HourConfigBuilder.Companion.hourConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DayHourConfigTest : AbstractConfigTest<DayHourConfig>() {

  override val serializer: KSerializer<DayHourConfig> = DayHourConfig.serializer()

  override val configByConstructor: DayHourConfig = DayHourConfig(
    DayConfig(changeDayAfterZi = false , midnight = DayConfig.MidnightImpl.CLOCK0),
    hourImpl = DayHourConfig.HourImpl.LMT
  )

  override val configByFunction = hourConfig {
    dayConfig {
      changeDayAfterZi = false
      midnight = DayConfig.MidnightImpl.CLOCK0
    }
    hourImpl = DayHourConfig.HourImpl.LMT
  }

  override val assertion = { raw: String ->
    logger.info { raw }
    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT""".toRegex()))
  }
}
