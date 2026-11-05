/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.DayNightConfigBuilder.Companion.dayNight
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

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
    assertEquals("Half", JsonPath.read<String>(raw, "$.dayNightImpl"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.transConfig.pressure"))
  }
}
