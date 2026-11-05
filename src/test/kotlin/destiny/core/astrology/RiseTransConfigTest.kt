/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.RiseTransConfigBuilder.Companion.riseTrans
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class RiseTransConfigTest : AbstractConfigTest<RiseTransConfig>() {

  override val serializer: KSerializer<RiseTransConfig> = RiseTransConfig.serializer()

  override val configByConstructor: RiseTransConfig = RiseTransConfig(Planet.MOON,
                                                                      TransPoint.MERIDIAN,
                                                                      TransConfig(
                                                                        discCenter = true,
                                                                        refraction = false,
                                                                        temperature = 23.0,
                                                                        pressure = 1000.0
                                                                      ))
  override val configByFunction: RiseTransConfig = riseTrans {
    star = Planet.MOON
    transPoint = TransPoint.MERIDIAN
    trans {
      discCenter = true
      refraction = false
      temperature = 23.0
      pressure = 1000.0
    }
  }

  override val assertion: (String) -> Unit = {raw ->
    assertEquals("Planet.MOON", JsonPath.read<String>(raw, "$.star"))
    assertEquals("MERIDIAN", JsonPath.read<String>(raw, "$.transPoint"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.transConfig.pressure"))
  }
}
