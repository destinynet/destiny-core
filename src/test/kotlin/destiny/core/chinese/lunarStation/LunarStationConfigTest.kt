/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfig
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.chinese.YearType
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class LunarStationConfigTest : AbstractConfigTest<LunarStationConfig>() {

  override val serializer: KSerializer<LunarStationConfig> = LunarStationConfig.serializer()

  override val configByConstructor: LunarStationConfig? = null
//    LunarStationConfig(
//    yearlyConfig = YearlyConfig(yearType = YearType.YEAR_LUNAR),
//    monthlyConfig = MonthlyConfig(MonthlyImpl.AnimalExplained, MonthAlgo.MONTH_FIXED_THIS),
//  )

  override val configByFunction: LunarStationConfig
    get() {
      val ewConfig = EightWordsConfig(
        dayHourConfig = DayHourConfig(
          hourBranchConfig = HourBranchConfig(
            transConfig = TransConfig(
              discCenter = true,
              refraction = false
            )
          )
        )
      )

      return LunarStationConfig(
        MonthlyConfig(
          yearlyConfig = YearlyConfig(
            yearType = YearType.YEAR_LUNAR
          ),
          monthlyImpl = MonthlyImpl.AnimalExplained,
          monthAlgo = MonthAlgo.MONTH_FIXED_THIS
        ),
        HourlyConfig(
          hourlyImpl = HourlyImpl.Fixed
        ),
        //ewConfig = ewConfig
      )
    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""monthlyImpl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""monthAlgo":\s*"MONTH_FIXED_THIS"""".toRegex()))
  }

}
