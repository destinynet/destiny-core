/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfig
import destiny.core.calendar.eightwords.HourBranchConfigBuilder.Companion.hourBranchConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HourBranchConfigTest : AbstractConfigTest<HourBranchConfig>() {
  override val serializer: KSerializer<HourBranchConfig> = HourBranchConfig.serializer()

  override val configByConstructor: HourBranchConfig = HourBranchConfig(HourBranchConfig.HourImpl.TST , TransConfig(discCenter = true,
                                                                                                                    refraction = false,
                                                                                                                    temperature = 23.0,
                                                                                                                    pressure = 1000.0)
  )

  override val configByFunction: HourBranchConfig = hourBranchConfig {
    tst {
      discCenter = true
      refraction = false
      temperature = 23.0
      pressure = 1000.0
    }
  }

  override val assertion: (String) -> Unit = {raw ->
    logger.info { raw }
    assertTrue(raw.contains(""""hourImpl":\s*"TST"""".toRegex()))
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }

}
