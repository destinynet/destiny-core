/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class HouseConfigTest : AbstractConfigTest<HouseConfig>() {

  override val serializer: KSerializer<HouseConfig> = HouseConfig.serializer()

  override val configByConstructor = HouseConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL)

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals("EQUAL", JsonPath.read<String>(raw, "$.houseSystem"))
    assertEquals("SIDEREAL", JsonPath.read<String>(raw, "$.coordinate"))
  }
}
