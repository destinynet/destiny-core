/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.chinese.lunarStation.MonthlyConfigBuilder.Companion.monthly
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class MonthlyConfigTest : AbstractConfigTest<MonthlyConfig>() {
  override val serializer: KSerializer<MonthlyConfig> = MonthlyConfig.serializer()

  override val configByConstructor: MonthlyConfig = MonthlyConfig(MonthlyImpl.AnimalExplained , MonthAlgo.MONTH_LEAP_SPLIT15)

  override val configByFunction: MonthlyConfig = monthly {
    impl = MonthlyImpl.AnimalExplained
    monthAlgo = MonthAlgo.MONTH_LEAP_SPLIT15
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""impl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_LEAP_SPLIT15"""".toRegex()))
  }
}
