/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.DayNightConfigBuilder.Companion.dayNight
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DayNightConfigTest : AbstractConfigTest<DayNightConfig>() {
  override val serializer: KSerializer<DayNightConfig> = DayNightConfig.serializer()

  override val configByConstructor: DayNightConfig = DayNightConfig(
    impl = DayNightImpl.Half, TransConfig(
      discCenter = true,
      refraction = false,
      temperature = 23.0,
      pressure = 1000.0
    )
  )

  override val configByFunction: DayNightConfig = dayNight {
    impl = DayNightImpl.Half
    trans {
      discCenter = true
      refraction = false
      temperature = 23.0
      pressure = 1000.0
    }
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"Half"""".toRegex()))
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
