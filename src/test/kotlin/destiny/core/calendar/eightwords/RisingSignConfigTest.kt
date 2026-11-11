/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.HouseConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class RisingSignConfigTest : AbstractConfigTest<RisingSignConfig>() {

  override val serializer: KSerializer<RisingSignConfig> = RisingSignConfig.serializer()

  override val configByConstructor = RisingSignConfig(
    HouseConfig(),
    TradChineseRisingSignConfig(HourImpl.LMT),
    RisingSignImpl.TradChinese
  )

  override val assertion = { raw: String ->
    assertNotNull(JsonPath.read<Any>(raw, "$.tradChineseRisingSignConfig"))
    assertEquals("LMT", JsonPath.read<String>(raw, "$.tradChineseRisingSignConfig.hourImpl"))
  }

}
