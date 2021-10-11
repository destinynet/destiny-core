/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.LunarStationConfigBuilder.Companion.lunarStation
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class LunarStationConfigTest : AbstractConfigTest<LunarStationConfig>() {

  override val serializer: KSerializer<LunarStationConfig> = LunarStationConfig.serializer()

  override val configByConstructor: LunarStationConfig = LunarStationConfig(
    yearlyConfig = YearlyConfig(yearType = YearType.YEAR_LUNAR),
    monthlyConfig = MonthlyConfig(MonthlyImpl.AnimalExplained),
    monthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS
  )

  override val configByFunction: LunarStationConfig = lunarStation {
    yearly {
      yearType = YearType.YEAR_LUNAR
    }
    monthly {
      impl = MonthlyImpl.AnimalExplained
    }
    monthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""impl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_FIXED_THIS"""".toRegex()))
  }

}
