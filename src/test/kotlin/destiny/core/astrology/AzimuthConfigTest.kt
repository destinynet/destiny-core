/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.AzimuthConfigBuilder.Companion.azimuth
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class AzimuthConfigTest : AbstractConfigTest<AzimuthConfig>() {
  override val serializer: KSerializer<AzimuthConfig> = AzimuthConfig.serializer()

  override val configByConstructor: AzimuthConfig = AzimuthConfig(Planet.MOON, Coordinate.SIDEREAL , 100.0 , 23.0 , 1000.0, StarTypeOptions.DEFAULT)

  override val configByFunction: AzimuthConfig = azimuth {
    star = Planet.MOON
    coordinate = Coordinate.SIDEREAL
    geoAlt = 100.0
    temperature = 23.0
    pressure = 1000.0
    starTypeOptions = StarTypeOptions.DEFAULT
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""star":\s*"Planet.MOON"""".toRegex()))
    assertTrue(raw.contains(""""coordinate":\s*"SIDEREAL"""".toRegex()))
    assertTrue(raw.contains(""""geoAlt":\s*100.0""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
    assertTrue(raw.contains(""""nodeType":\s*"MEAN"""".toRegex()))
    assertTrue(raw.contains(""""apsisType":\s*"MEAN"""".toRegex()))
  }
}
