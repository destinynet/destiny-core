/**
 * Created by smallufo on 2021-09-27.
 */
package destiny.core.iching.divine

import destiny.core.AbstractConfigTest
import destiny.core.iching.divine.DivineConfigBuilder.Companion.divineConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DivineConfigTest : AbstractConfigTest<DivineConfig>() {
  override val serializer: KSerializer<DivineConfig> = DivineConfig.serializer()

  override val configByConstructor: DivineConfig = DivineConfig(
    approach = DivineApproach.RANDOM,
    place = "台北市"
  )

  override val configByFunction: DivineConfig = divineConfig {
    approach = DivineApproach.RANDOM
    place = "台北市"
  }

  override val assertion: (String) -> Unit = {raw : String ->
    assertTrue(raw.contains(""""approach":\s*"RANDOM"""".toRegex()))
    assertTrue(raw.contains(""""place":\s*"台北市"""".toRegex()))
  }

}
