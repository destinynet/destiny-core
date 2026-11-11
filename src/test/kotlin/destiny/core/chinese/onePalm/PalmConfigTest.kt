/**
 * Created by smallufo on 2021-09-20.
 */
package destiny.core.chinese.onePalm

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.EightWordsConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class PalmConfigTest : AbstractConfigTest<PalmConfig>() {

  override val serializer: KSerializer<PalmConfig> = PalmConfig.serializer()

  override val configByConstructor: PalmConfig = PalmConfig(EightWordsConfig(),
                                                            PositiveImpl.GenderYinYang,
                                                            MonthAlgo.MONTH_SOLAR_TERMS,
                                                            trueRisingSign = true,
                                                            clockwiseHouse = false)

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals("GenderYinYang", JsonPath.read<String>(raw, "$.positiveImpl"))
    assertEquals("MONTH_SOLAR_TERMS", JsonPath.read<String>(raw, "$.monthAlgo"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.trueRisingSign"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.clockwiseHouse"))
  }
}
