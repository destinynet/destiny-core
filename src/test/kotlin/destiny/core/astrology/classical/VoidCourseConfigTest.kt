/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology.classical

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.Centric
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.VoidCourseConfigBuilder.Companion.voidCourse
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class VoidCourseConfigTest : AbstractConfigTest<VoidCourseConfig>() {
  override val serializer: KSerializer<VoidCourseConfig> = VoidCourseConfig.serializer()

  override val configByConstructor: VoidCourseConfig = VoidCourseConfig(Planet.VENUS, Centric.TOPO, VoidCourseImpl.WilliamLilly)

  override val configByFunction: VoidCourseConfig = voidCourse {
    planet = Planet.VENUS
    centric = Centric.TOPO
    impl = VoidCourseImpl.WilliamLilly
  }

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("Planet.VENUS", JsonPath.read(raw, "$.planet"))
    assertEquals("TOPO", JsonPath.read(raw, "$.centric"))
    assertEquals("WilliamLilly", JsonPath.read(raw, "$.vocImpl"))
  }
}
