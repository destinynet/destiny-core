/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.calendar.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfig
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import destiny.core.calendar.eightwords.HourBranchConfigBuilder.Companion.hourBranchConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class HourBranchConfigTest : AbstractConfigTest<HourBranchConfig>() {
  override val serializer: KSerializer<HourBranchConfig> = HourBranchConfig.serializer()

  override val configByConstructor: HourBranchConfig = HourBranchConfig(
    HourImpl.TST, TransConfig(
      discCenter = true,
      refraction = false,
      temperature = 23.0,
      pressure = 1000.0
    )
  )

  override val configByFunction: HourBranchConfig
    get() {
      val transConfig = trans {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0
      }

      return with(transConfig) {
        hourBranchConfig {
          hourImpl = HourImpl.TST
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("TST", JsonPath.read<String>(raw, "$.hourImpl"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.transConfig.pressure"))
  }

}
