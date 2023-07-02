/**
 * Created by smallufo on 2021-09-20.
 */
package destiny.core.chinese.onePalm

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.onePalm.PalmConfigBuilder.Companion.palmConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class PalmConfigTest : AbstractConfigTest<PalmConfig>() {

  override val serializer: KSerializer<PalmConfig> = PalmConfig.serializer()

  override val configByConstructor: PalmConfig = PalmConfig(EightWordsConfig(),
                                                            PositiveImpl.GenderYinYang,
                                                            MonthAlgo.MONTH_SOLAR_TERMS,
                                                            trueRisingSign = true,
                                                            clockwiseHouse = false)
  override val configByFunction: PalmConfig
    get() {
      return with(EightWordsConfig()) {
        palmConfig {
          positiveImpl = PositiveImpl.GenderYinYang
          monthAlgo = MonthAlgo.MONTH_SOLAR_TERMS
          trueRisingSign = true
          clockwiseHouse = false
        }
      }
    }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""positiveImpl":\s*"GenderYinYang"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_SOLAR_TERMS"""".toRegex()))
    assertTrue(raw.contains(""""trueRisingSign":\s*true""".toRegex()))
    assertTrue(raw.contains(""""clockwiseHouse":\s*false""".toRegex()))
  }
}
