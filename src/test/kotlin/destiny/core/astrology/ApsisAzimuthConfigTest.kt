/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.ApsisAzimuthConfigBuilder.Companion.apsisAzimuth
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class ApsisAzimuthConfigTest : AbstractConfigTest<ApsisAzimuthConfig>() {

  override val serializer: KSerializer<ApsisAzimuthConfig> = ApsisAzimuthConfig.serializer()

  override val configByConstructor: ApsisAzimuthConfig = ApsisAzimuthConfig(Planet.VENUS,
                                                                            Coordinate.SIDEREAL,
                                                                            NodeType.TRUE,
                                                                            temperature = 23.5,
                                                                            pressure = 1000.0)

  override val configByFunction: ApsisAzimuthConfig = apsisAzimuth {
    star = Planet.VENUS
    coordinate = Coordinate.SIDEREAL
    nodeType = NodeType.TRUE
    temperature = 23.5
    pressure = 1000.0
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""star":\s*"Planet.VENUS"""".toRegex()))
    assertTrue(raw.contains(""""coordinate":\s*"SIDEREAL"""".toRegex()))
    assertTrue(raw.contains(""""nodeType":\s*"TRUE"""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.5""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
