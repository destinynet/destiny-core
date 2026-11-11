/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class ApsisAzimuthConfigTest : AbstractConfigTest<ApsisAzimuthConfig>() {

  override val serializer: KSerializer<ApsisAzimuthConfig> = ApsisAzimuthConfig.serializer()

  override val configByConstructor: ApsisAzimuthConfig = ApsisAzimuthConfig(Planet.VENUS,
                                                                            Coordinate.SIDEREAL,
                                                                            NodeType.TRUE,
                                                                            temperature = 23.5,
                                                                            pressure = 1000.0)

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("Planet.VENUS", JsonPath.read<String>(raw, "$.star"))
    assertEquals("SIDEREAL", JsonPath.read<String>(raw, "$.coordinate"))
    assertEquals("TRUE", JsonPath.read<String>(raw, "$.nodeType"))
    assertEquals(23.5, JsonPath.read<Double>(raw, "$.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.pressure"))
  }
}
