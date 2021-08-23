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
      monthImpl = MonthConfig.MonthImpl.SunSign
    )
  )

  override val configByFunction = yearMonthConfig {
    year {
      changeYearDegree = 270.0
    }
    month {
      southernHemisphereOpposition = true
      hemisphereBy = HemisphereBy.DECLINATION
      monthImpl = MonthConfig.MonthImpl.SunSign
    }
  }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))
  }

}
