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
import destiny.core.chinese.lunarStation.*
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DailyReportConfigTest : AbstractConfigTest<DailyReportConfig>() {

  override val serializer: KSerializer<DailyReportConfig> = DailyReportConfig.serializer()

  override val configByConstructor = null
//    DailyReportConfig(
//    LunarStationConfig(
//      MonthlyConfig(
//        monthlyImpl = MonthlyImpl.AnimalExplained,
//        yearlyConfig = YearlyConfig(YearType.YEAR_LUNAR),
//        dayHourConfig = DayHourConfig()
//      ),
//      HourlyConfig(
//        HourlyImpl.Fixed, DayHourConfig(
//          hourBranchConfig = HourBranchConfig(
//            transConfig = TransConfig(
//              discCenter = true,
//              refraction = false
//            )
//          )
//        )
//      )
//    ),
//    VoidCourseConfig(Planet.VENUS, Centric.TOPO, VoidCourseImpl.WilliamLilly)
//  )

  override val configByFunction: DailyReportConfig
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


      val lsConfig = LunarStationConfig(
        MonthlyConfig(
          yearlyConfig = YearlyConfig(
            yearType = YearType.YEAR_LUNAR
          ),
          monthlyImpl = MonthlyImpl.AnimalExplained
        ),
        HourlyConfig(
          hourlyImpl = HourlyImpl.Fixed
        ),

        //ewConfig = ewConfig
      )


      return with(lsConfig) {
        dailyReport {
          discCenter = true
          refraction = false
          vocConfig {
            planet = Planet.VENUS
            centric = Centric.TOPO
            impl = VoidCourseImpl.WilliamLilly
          }
        }
      }


    }


  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""yearType":\s*"YEAR_LUNAR"""".toRegex()))
    assertTrue(raw.contains(""""monthlyImpl":\s*"AnimalExplained"""".toRegex()))
    assertTrue(raw.contains(""""hourlyImpl":\s*"Fixed"""".toRegex()))
    assertTrue(raw.contains(""""planet":\s*"Planet.VENUS"""".toRegex()))
    assertTrue(raw.contains(""""centric":\s*"TOPO"""".toRegex()))
    assertTrue(raw.contains(""""vocImpl":\s*"WilliamLilly"""".toRegex()))

    // FIXME 以下 failed , 與 IMonthlyConfig 沒有 extend IDayHourConfig 有關
    assertFalse(raw.contains(""""discCenter":\s*false""".toRegex()))
    assertFalse(raw.contains(""""refraction":\s*true""".toRegex()))
  }
}
