/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.RiseTransConfigBuilder.Companion.riseTrans
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class RiseTransConfigTest : AbstractConfigTest<RiseTransConfig>() {

  override val serializer: KSerializer<RiseTransConfig> = RiseTransConfig.serializer()

  override val configByConstructor: RiseTransConfig = RiseTransConfig(Planet.MOON,
                                                                      TransPoint.MERIDIAN,
                                                                      TransConfig(
                                                                        discCenter = true,
                                                                        refraction = false,
                                                                        temperature = 23.0,
                                                                        pressure = 1000.0
                                                                      ))
  override val configByFunction: RiseTransConfig = riseTrans {
    star = Planet.MOON
    transPoint = TransPoint.MERIDIAN
    trans {
      discCenter = true
      refraction = false
      temperature = 23.0
      pressure = 1000.0
    }
  }

  override val assertion: (String) -> Unit = {raw ->
    logger.info { raw }
    assertTrue(raw.contains(""""star":\s*"Planet.MOON""".toRegex()))
    assertTrue(raw.contains(""""transPoint":\s*"MERIDIAN""".toRegex()))
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
