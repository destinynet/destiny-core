/**
 * Created by smallufo on 2021-08-09.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.MonthConfigBuilder.Companion.monthConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MonthConfigTest {

  val logger = KotlinLogging.logger { }

  private val configByConstruction = MonthConfig(
    YearConfig(270.0),
    southernHemisphereOpposition = true,
    hemisphereBy = HemisphereBy.DECLINATION,
    impl = MonthConfig.Impl.SunSign
  )

  private val configByFunction = monthConfig {
    yearConfig {
      changeYearDegree = 270.0
    }
    southernHemisphereOpposition = true
    hemisphereBy = HemisphereBy.DECLINATION
    monthImpl = MonthConfig.Impl.SunSign
  }

  @Test
  fun testEquals() {
    assertEquals(configByConstruction, configByFunction)
  }

  @Test
  fun testSerialize() {
    val assertion = { raw: String ->
      logger.info { raw }
      assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
      assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
      assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION""".toRegex()))
      assertTrue(raw.contains(""""impl":\s*"SunSign""".toRegex()))
    }

    Json {
      encodeDefaults = true
      prettyPrint = true
    }.also { prettyFormat ->
      prettyFormat.encodeToString(configByConstruction).also(assertion)
    }

    Json {
      encodeDefaults = true
      prettyPrint = false
    }.also { denseFormat ->
      denseFormat.encodeToString(configByFunction).also(assertion)
    }
  }
}
