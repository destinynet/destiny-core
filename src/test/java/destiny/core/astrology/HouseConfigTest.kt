/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.HouseConfigBuilder.Companion.houseCusp
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HouseConfigTest : AbstractConfigTest<HouseConfig>() {

  override val serializer: KSerializer<HouseConfig> = HouseConfig.serializer()

  override val configByConstructor = HouseConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL)

  override val configByFunction = houseCusp {
    houseSystem = HouseSystem.EQUAL
    coordinate = Coordinate.SIDEREAL
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""houseSystem":\s*"EQUAL"""".toRegex()))
    assertTrue(raw.contains(""""coordinate":\s*"SIDEREAL"""".toRegex()))
  }
}
