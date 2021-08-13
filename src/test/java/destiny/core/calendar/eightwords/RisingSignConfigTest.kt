/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.RisingSignConfigBuilder.Companion.risingSign
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RisingSignConfigTest {
  val logger = KotlinLogging.logger { }

  private val configByConstructor = RisingSignConfig(
    HouseCuspConfig(),
    TradChineseRisingSignConfig(DayHourConfig.HourImpl.LMT),
    RisingSignConfig.Impl.TradChinese
  )

  private val configByFunction = risingSign {

    tradChinese {
      hourImpl = DayHourConfig.HourImpl.LMT
    }
  }

  @Test
  fun testEquals() {
    assertEquals(configByConstructor, configByFunction)
  }

  @Test
  fun testSerialize() {
    val assertion = { raw: String ->
      logger.info { raw }
      assertTrue(raw.contains(""""tradChineseRisingSignConfig""".toRegex()))
      assertTrue(raw.contains(""""hourImpl":\s*"LMT""".toRegex()))
    }

    Json {
      encodeDefaults = true
      prettyPrint = true
    }.also { format ->
      assertAndCompareDecoded(format, configByConstructor, assertion)
    }

    Json {
      encodeDefaults = true
      prettyPrint = false
    }.also { format ->
      assertAndCompareDecoded(format, configByFunction, assertion)
    }
  }
}
