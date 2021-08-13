/**
 * Created by smallufo on 2021-08-10.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.HourConfigBuilder.Companion.hourConfig
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DayHourConfigTest {
  val logger = KotlinLogging.logger { }

  private val configByConstructor: DayHourConfig = DayHourConfig(
    DayConfig(changeDayAfterZi = false , midnight = DayConfig.MidnightImpl.CLOCK0),
    hourImpl = DayHourConfig.HourImpl.LMT
  )

  private val configByFunction = hourConfig {
    dayConfig {
      changeDayAfterZi = false
      midnight = DayConfig.MidnightImpl.CLOCK0
    }
    hourImpl = DayHourConfig.HourImpl.LMT
  }

  @Test
  fun testEquals() {
    assertEquals(configByConstructor, configByFunction)
  }



  @Test
  fun testSerialize() {

    val assertion = { raw: String ->
      logger.info { raw }
      assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
      assertTrue(raw.contains(""""midnight":\s*"CLOCK0""".toRegex()))
      assertTrue(raw.contains(""""hourImpl":\s*"LMT""".toRegex()))
    }

    Json {
      encodeDefaults = true
      prettyPrint = true
    }.also { format: Json ->
      assertAndCompareDecoded(format, configByConstructor , assertion)
    }

    Json {
      encodeDefaults = true
      prettyPrint = false
    }.also { format ->
      assertAndCompareDecoded(format, configByFunction , assertion)
    }
  }
}
