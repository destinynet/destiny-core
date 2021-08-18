/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.YearlyConfigBuilder.Companion.yearly
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class YearlyConfigTest : AbstractConfigTest<YearlyConfig>() {

  override val serializer: KSerializer<YearlyConfig> = YearlyConfig.serializer()

  override val configByConstructor: YearlyConfig = YearlyConfig(YearType.YEAR_LUNAR, YearEpoch.EPOCH_1864)

  override val configByFunction: YearlyConfig = yearly {
    yearType = YearType.YEAR_LUNAR
    yearEpoch = YearEpoch.EPOCH_1864
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""yearEpoch":\s*"EPOCH_1864"""".toRegex()))
  }
}
