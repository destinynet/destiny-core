/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.MonthConfigBuilder.Companion.monthConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue


class MonthConfigTest : AbstractConfigTest<MonthConfig>() {
  override val serializer: KSerializer<MonthConfig> = MonthConfig.serializer()

  override val configByConstructor: MonthConfig = MonthConfig(
    southernHemisphereOpposition = true,
    hemisphereBy = HemisphereBy.DECLINATION,
    monthImpl = MonthImpl.SunSign
  )

  override val configByFunction: MonthConfig = monthConfig {
    southernHemisphereOpposition = true
    hemisphereBy = HemisphereBy.DECLINATION
    monthImpl = MonthImpl.SunSign
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))
  }
}
