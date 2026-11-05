/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.AzimuthConfigBuilder.Companion.azimuth
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class AzimuthConfigTest : AbstractConfigTest<AzimuthConfig>() {
  override val serializer: KSerializer<AzimuthConfig> = AzimuthConfig.serializer()

  override val configByConstructor: AzimuthConfig = AzimuthConfig(Planet.MOON, Coordinate.SIDEREAL , 100.0 , 23.0 , 1000.0, StarTypeOptions.MEAN)

  override val configByFunction: AzimuthConfig = azimuth {
    star = Planet.MOON
    coordinate = Coordinate.SIDEREAL
    geoAlt = 100.0
    temperature = 23.0
    pressure = 1000.0
    starTypeOptions = StarTypeOptions.MEAN
  }

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("Planet.MOON", JsonPath.read<String>(raw, "$.star"))
    assertEquals("SIDEREAL", JsonPath.read<String>(raw, "$.coordinate"))
    assertEquals(100.0, JsonPath.read<Double>(raw, "$.geoAlt"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.pressure"))
    assertEquals("MEAN", JsonPath.read<String>(raw, "$.starTypeOptions.nodeType"))
    assertEquals("MEAN", JsonPath.read<String>(raw, "$.starTypeOptions.apsisType"))
  }
}
