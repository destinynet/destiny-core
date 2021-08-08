/**
 * Created by smallufo on 2021-08-09.
 */
package destiny.core.calendar.eightwords

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertTrue

internal class MonthConfigTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testSerialize() {
    val config = MonthConfig()

    val assertion = { raw: String ->
      logger.info { raw }
      assertTrue(raw.contains(""""southernHemisphereOpposition":\s*false""".toRegex()))
      assertTrue(raw.contains(""""hemisphereBy":\s*"EQUATOR""".toRegex()))
      assertTrue(raw.contains(""""impl":\s*"SolarTerms""".toRegex()))
    }

    Json {
      encodeDefaults = true
      prettyPrint = true
    }.also { prettyFormat ->
      prettyFormat.encodeToString(config).also(assertion)
    }

    Json {
      encodeDefaults = true
      prettyPrint = false
    }.also { denseFormat ->
      denseFormat.encodeToString(config).also(assertion)
    }
  }
}
