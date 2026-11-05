/**
 * Created by smallufo on 2022-10-19.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.PlanetaryHourConfigBuilder.Companion.planetaryHour
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class PlanetaryHourConfigTest : AbstractConfigTest<PlanetaryHourConfig>() {

  override val serializer: KSerializer<PlanetaryHourConfig> = PlanetaryHourConfig.serializer()

  override val configByConstructor: PlanetaryHourConfig = PlanetaryHourConfig(PlanetaryHourType.CLOCK, TransConfig(
    discCenter = true,
    refraction = false,
    temperature = 12.3,
    pressure = 1000.0
  ))

  override val configByFunction: PlanetaryHourConfig = planetaryHour {
    type = PlanetaryHourType.CLOCK
    transConfig {
      discCenter = true
      refraction = false
      temperature = 12.3
      pressure = 1000.0
    }
  }

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("CLOCK", JsonPath.read<String>(raw, "$.type"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.transConfig.refraction"))
    assertEquals(12.3, JsonPath.read<Double>(raw, "$.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.transConfig.pressure"))
  }

}
