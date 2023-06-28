/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfigBuilder.Companion.trans
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue


class TransConfigTest : AbstractConfigTest<TransConfig>() {

  override val serializer: KSerializer<TransConfig> = TransConfig.serializer()

  override val configByConstructor: TransConfig = TransConfig(discCenter = true,
                                                              refraction = false,
                                                              temperature = 23.0,
                                                              pressure = 1000.0)
  override val configByFunction: TransConfig = trans {
    discCenter = true
    refraction = false
    temperature = 23.0
    pressure = 1000.0
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
