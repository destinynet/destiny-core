/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.YearlyConfigBuilder.Companion.yearly
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class YearlyConfigTest : AbstractConfigTest<YearlyConfig>() {

  override val serializer: KSerializer<YearlyConfig> = YearlyConfig.serializer()

  override val configByConstructor: YearlyConfig? = null

  override val configByFunction: YearlyConfig
    get() {

      return with(DayHourConfig()) {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0

        yearly {
          yearType = YearType.YEAR_LUNAR
          yearEpoch = YearEpoch.EPOCH_1864
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""yearEpoch":\s*"EPOCH_1864"""".toRegex()))

    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))

    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))

    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
  }
}
