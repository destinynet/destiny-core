/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.HiddenVenusFoeConfigBuilder.Companion.hiddenVenusFoe
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HiddenVenusFoeConfigTest : AbstractConfigTest<HiddenVenusFoeConfig>() {

  override val serializer: KSerializer<HiddenVenusFoeConfig> = HiddenVenusFoeConfig.serializer()

  override val configByConstructor: HiddenVenusFoeConfig = HiddenVenusFoeConfig(
    yearlyConfig = YearlyConfig(yearType = YearType.YEAR_LUNAR),
    monthlyConfig = MonthlyConfig(MonthlyConfig.Impl.AnimalExplained),
    monthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS
  )

  override val configByFunction: HiddenVenusFoeConfig = hiddenVenusFoe {
    yearly {
      yearType = YearType.YEAR_LUNAR
    }
    monthly {
      impl = MonthlyConfig.Impl.AnimalExplained
    }
    monthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""impl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_FIXED_THIS"""".toRegex()))
  }
}
