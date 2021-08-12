/**
 * Created by smallufo on 2021-08-09.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.MonthConfigBuilder.Companion.monthConfig
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class YearMonthConfigTest {

  val logger = KotlinLogging.logger { }

  private val configByConstructor = YearMonthConfig(
    YearConfig(270.0),
    southernHemisphereOpposition = true,
    hemisphereBy = HemisphereBy.DECLINATION,
    impl = YearMonthConfig.Impl.SunSign
  )

  private val configByFunction = monthConfig {
    yearConfig {
      changeYearDegree = 270.0
    }
    southernHemisphereOpposition = true
    hemisphereBy = HemisphereBy.DECLINATION
    monthImpl = YearMonthConfig.Impl.SunSign
  }

  @Test
  fun testEquals() {
    assertEquals(configByConstructor, configByFunction)
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
