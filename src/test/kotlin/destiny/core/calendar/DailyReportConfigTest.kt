/**
 * Created by smallufo on 2022-02-13.
 */
package destiny.core.calendar

import destiny.core.AbstractConfigTest
import destiny.core.astrology.Centric
import destiny.core.astrology.Planet
import destiny.core.astrology.TransConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.calendar.DailyReportConfigBuilder.Companion.dailyReport
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.HourlyImpl
import destiny.core.chinese.lunarStation.MonthlyImpl
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class DailyReportConfigTest : AbstractConfigTest<DailyReportConfig>() {

  override val serializer: KSerializer<DailyReportConfig> = DailyReportConfig.serializer()

  override val configByConstructor = null
//  = DailyReportConfig(
//    LunarStationConfig(
//      YearlyConfig(YearType.YEAR_LUNAR),
//      MonthlyConfig(MonthlyImpl.AnimalExplained),
//      HourlyConfig(HourlyImpl.Fixed, DayHourConfig(hourBranchConfig = HourBranchConfig(
//        transConfig = TransConfig(
//          discCenter = true,
//          refraction = false
//        )
//      )))
//    ),
//    VoidCourseConfig(Planet.VENUS, Centric.TOPO , VoidCourseImpl.WilliamLilly)
//  )

  override val configByFunction: DailyReportConfig = with(
    EightWordsConfig(
      dayHourConfig = DayHourConfig(
        hourBranchConfig = HourBranchConfig(
          transConfig = TransConfig(
            discCenter = true,
            refraction = false
          )
        )
      )
    )
  ) {
    dailyReport {
      yearType = YearType.YEAR_LUNAR

      monthlyImpl = MonthlyImpl.AnimalExplained

      hourlyImpl = HourlyImpl.Fixed

      vocConfig {
        planet = Planet.VENUS
        centric = Centric.TOPO
        impl = VoidCourseImpl.WilliamLilly
      }
    }
  }


  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""impl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""impl":\s*"Fixed"""".toRegex()))

    assertTrue(raw.contains(""""planet":\s*"Planet.VENUS"""".toRegex()))
    assertTrue(raw.contains(""""centric":\s*"TOPO"""".toRegex()))
    assertTrue(raw.contains(""""vocImpl":\s*"WilliamLilly"""".toRegex()))
  }
}
