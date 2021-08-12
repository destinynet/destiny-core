/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.calendar.eightwords.RisingSignConfigBuilder.Companion.risingSign
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RisingSignConfigTest {
  val logger = KotlinLogging.logger { }

  private val configByConstructor = RisingSignConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL, RisingSignConfig.Impl.TradChinese)

  private val configByFunction = risingSign {
    houseSystem = HouseSystem.EQUAL
    coordinate = Coordinate.SIDEREAL
    impl = RisingSignConfig.Impl.TradChinese
  }

  @Test
  fun testEquals() {
    assertEquals(configByConstructor, configByFunction)
  }

  @Test
  fun testSerialize() {
    val assertion = { raw: String ->
      logger.info { raw }
      assertTrue(raw.contains(""""houseSystem":\s*"EQUAL""".toRegex()))
      assertTrue(raw.contains(""""coordinate":\s*"SIDEREAL""".toRegex()))
      assertTrue(raw.contains(""""impl":\s*"TradChinese""".toRegex()))
    }

    Json {
      encodeDefaults = true
      prettyPrint = true
    }.also { format ->
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
