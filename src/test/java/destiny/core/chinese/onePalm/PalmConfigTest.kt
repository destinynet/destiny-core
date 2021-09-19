/**
 * Created by smallufo on 2021-09-20.
 */
package destiny.core.chinese.onePalm

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.chinese.onePalm.PalmConfigBuilder.Companion.palmConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class PalmConfigTest : AbstractConfigTest<PalmConfig>() {

  override val serializer: KSerializer<PalmConfig> = PalmConfig.serializer()

  override val configByConstructor: PalmConfig = PalmConfig(PalmConfig.PositiveImpl.GenderYinYang,
                                                            IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS,
                                                            trueRisingSign = true,
                                                            clockwiseHouse = false)
  override val configByFunction: PalmConfig = palmConfig {
    positiveImpl = PalmConfig.PositiveImpl.GenderYinYang
    monthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS
    trueRisingSign = true
    clockwiseHouse = false
  }

  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue(raw.contains(""""positiveImpl":\s*"GenderYinYang"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_SOLAR_TERMS"""".toRegex()))
    assertTrue(raw.contains(""""trueRisingSign":\s*true""".toRegex()))
    assertTrue(raw.contains(""""clockwiseHouse":\s*false""".toRegex()))
  }
}
