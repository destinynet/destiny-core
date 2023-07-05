/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.LunarStationConfigBuilder.Companion.lunarStation
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LunarStationConfigTest : AbstractConfigTest<LunarStationConfig>() {

  override val serializer: KSerializer<LunarStationConfig> = LunarStationConfig.serializer()

  override val configByConstructor: LunarStationConfig? = null

  override val configByFunction: LunarStationConfig
    get() {
      return with(LunarStationConfig()) {
        lunarStation {
          discCenter = true
          refraction = false
          temperature = 23.0
          pressure = 1000.0

          yearType = YearType.YEAR_LUNAR
          yearEpoch = YearEpoch.EPOCH_1864

          monthlyImpl = MonthlyImpl.AnimalExplained
          monthAlgo = MonthAlgo.MONTH_FIXED_THIS
          hourlyImpl = HourlyImpl.Fixed
        }
      }

    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertFalse(raw.contains(""""yearType":\s*"YEAR_SOLAR"""".toRegex()))

    assertTrue(raw.contains(""""yearEpoch":\s*"EPOCH_1864"""".toRegex()))
    assertFalse(raw.contains(""""yearEpoch":\s*"EPOCH_1564"""".toRegex()))

    assertTrue(raw.contains(""""monthlyImpl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_FIXED_THIS"""".toRegex()))

    assertTrue(raw.contains(""""hourlyImpl":\s*"Fixed"""".toRegex()))
    assertFalse(raw.contains(""""hourlyImpl":\s*"Yuan"""".toRegex()))

    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))

    // FIXME : 以下 failed
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))
  }

}
