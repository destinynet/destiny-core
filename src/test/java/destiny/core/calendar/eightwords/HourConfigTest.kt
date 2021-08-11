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

internal class HourConfigTest {
  val logger = KotlinLogging.logger { }

  private val configByConstructor: HourConfig = HourConfig(
    DayConfig(changeDayAfterZi = false),
    impl = HourConfig.Impl.LMT
  )

  private val configByFunction = hourConfig {
    dayConfig {
      changeDayAfterZi = false
    }
    hourImpl = HourConfig.Impl.LMT
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
      assertTrue(raw.contains(""""impl":\s*"LMT""".toRegex()))
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
