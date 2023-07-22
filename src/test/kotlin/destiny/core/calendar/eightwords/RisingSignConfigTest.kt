/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.HouseConfig
import destiny.core.calendar.eightwords.RisingSignConfigBuilder.Companion.risingSign
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class RisingSignConfigTest : AbstractConfigTest<RisingSignConfig>() {

  override val serializer: KSerializer<RisingSignConfig> = RisingSignConfig.serializer()

  override val configByConstructor = RisingSignConfig(
    HouseConfig(),
    TradChineseRisingSignConfig(HourImpl.LMT),
    RisingSignImpl.TradChinese
  )

  override val configByFunction: RisingSignConfig
    get() {
      return with(HouseConfig()) {
        risingSign {
          tradChinese {
            hourImpl = HourImpl.LMT
          }
        }
      }
    }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""tradChineseRisingSignConfig""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
  }

}
