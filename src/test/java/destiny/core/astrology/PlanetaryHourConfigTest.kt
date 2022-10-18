/**
 * Created by smallufo on 2022-10-19.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.PlanetaryHourConfigBuilder.Companion.planetaryHour
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

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
    assertTrue(raw.contains(""""type":\s*"CLOCK"""".toRegex()))
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*12\.3""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000\.0""".toRegex()))
  }

}
