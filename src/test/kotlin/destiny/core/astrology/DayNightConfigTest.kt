/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
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

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("Half", JsonPath.read<String>(raw, "$.dayNightImpl"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.transConfig.pressure"))
  }
}
