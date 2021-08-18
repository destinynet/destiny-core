/**
 * Created by smallufo on 2021-08-09.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.YearMonthConfigBuilder.Companion.yearMonthConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class YearMonthConfigTest : AbstractConfigTest<YearMonthConfig>() {

  override val serializer: KSerializer<YearMonthConfig> = YearMonthConfig.serializer()

  override val configByConstructor = YearMonthConfig(
    YearConfig(270.0),
    MonthConfig(
      southernHemisphereOpposition = true,
      hemisphereBy = HemisphereBy.DECLINATION,
      moonImpl = MonthConfig.MoonImpl.SunSign
    )
  )

  override val configByFunction = yearMonthConfig {
    year {
      changeYearDegree = 270.0
    }
    month {
      southernHemisphereOpposition = true
      hemisphereBy = HemisphereBy.DECLINATION
      monthImpl = MonthConfig.MoonImpl.SunSign
    }
  }

  override val assertion = { raw: String ->
    logger.info { raw }
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""moonImpl":\s*"SunSign"""".toRegex()))
  }

}
