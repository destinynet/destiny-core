/**
 * Created by smallufo on 2021-08-11.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.EightWordsConfigBuilder.Companion.ewConfig
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EightWordsConfigTest {
  val logger = KotlinLogging.logger { }

  private val configByConstructor: EightWordsConfig = EightWordsConfig(
    monthConfig = MonthConfig(
      YearConfig(270.0),
      southernHemisphereOpposition = true,
      hemisphereBy = HemisphereBy.DECLINATION,
      impl = MonthConfig.Impl.SunSign
    ),
    hourConfig = HourConfig(
      DayConfig(changeDayAfterZi = false),
      impl = HourConfig.Impl.LMT
    )
  )

  private val configByFunction: EightWordsConfig = ewConfig {
    monthConfig {
      yearConfig {
        changeYearDegree = 270.0
      }
      southernHemisphereOpposition = true
      hemisphereBy = HemisphereBy.DECLINATION
      monthImpl = MonthConfig.Impl.SunSign
    }

    hourConfig {
      dayConfig {
        changeDayAfterZi = false
      }
      hourImpl = HourConfig.Impl.LMT
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
      assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
      assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
      assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION""".toRegex()))
      assertTrue(raw.contains(""""impl":\s*"SunSign""".toRegex()))

      assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
      assertTrue(raw.contains(""""impl":\s*"LMT""".toRegex()))
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
