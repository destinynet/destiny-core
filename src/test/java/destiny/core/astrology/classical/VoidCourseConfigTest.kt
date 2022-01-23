/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology.classical

import destiny.core.AbstractConfigTest
import destiny.core.astrology.Centric
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.VoidCourseConfigBuilder.Companion.voidCourse
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class VoidCourseConfigTest : AbstractConfigTest<VoidCourseConfig>() {
  override val serializer: KSerializer<VoidCourseConfig> = VoidCourseConfig.serializer()

  override val configByConstructor: VoidCourseConfig = VoidCourseConfig(Planet.VENUS, Centric.TOPO, VoidCourseImpl.WilliamLilly)

  override val configByFunction: VoidCourseConfig = voidCourse {
    planet = Planet.VENUS
    centric = Centric.TOPO
    impl = VoidCourseImpl.WilliamLilly
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""planet":\s*"Planet.VENUS"""".toRegex()))
    assertTrue(raw.contains(""""centric":\s*"TOPO"""".toRegex()))
    assertTrue(raw.contains(""""vocImpl":\s*"WilliamLilly"""".toRegex()))
  }
}
