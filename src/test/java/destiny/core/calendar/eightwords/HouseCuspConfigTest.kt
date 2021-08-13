/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.calendar.eightwords.RisingSignConfigBuilder.HouseCuspConfigBuilder.Companion.houseCusp
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class HouseCuspConfigTest {
  val logger = KotlinLogging.logger { }

  private val configByConstructor = HouseCuspConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL)

  private val configByFunction = houseCusp {
    houseSystem = HouseSystem.EQUAL
    coordinate = Coordinate.SIDEREAL
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
