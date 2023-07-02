/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.DayNightConfigBuilder.Companion.dayNight
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DayNightConfigTest : AbstractConfigTest<DayNightConfig>() {
  override val serializer: KSerializer<DayNightConfig> = DayNightConfig.serializer()

  override val configByConstructor: DayNightConfig = DayNightConfig(
    dayNightImpl = DayNightImpl.Half, TransConfig(
      discCenter = true,
      refraction = false,
      temperature = 23.0,
      pressure = 1000.0
    )
  )

  override val configByFunction: DayNightConfig
    get() {

      val transConfig = trans {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0
      }

      return with(transConfig) {

        dayNight {
          dayNightImpl = DayNightImpl.Half
        }
      }

    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""dayNightImpl":\s*"Half"""".toRegex()))
    assertFalse(raw.contains(""""dayNightImpl":\s*"StarPos"""".toRegex()))

    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))

    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))

    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
