/**
 * Created by smallufo on 2022-02-13.
 */
package destiny.core.calendar

import destiny.core.AbstractConfigTest
import destiny.core.astrology.Centric
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.calendar.DailyReportConfigBuilder.Companion.dailyReport
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.HourlyImpl
import destiny.core.chinese.lunarStation.LunarStationConfig
import destiny.core.chinese.lunarStation.MonthlyImpl
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DailyReportConfigTest : AbstractConfigTest<DailyReportConfig>() {

  override val serializer: KSerializer<DailyReportConfig> = DailyReportConfig.serializer()

  override val configByConstructor = null

  override val configByFunction: DailyReportConfig
    get() {

      return with(LunarStationConfig()) {
        yearType = YearType.YEAR_LUNAR
        monthlyImpl = MonthlyImpl.AnimalExplained
        hourlyImpl = HourlyImpl.Fixed
        with(VoidCourseConfig()) {
          dailyReport {
            discCenter = true
            refraction = false
            temperature = 23.0
            pressure = 1000.0

            planet = Planet.VENUS
            centric = Centric.TOPO
            vocImpl = VoidCourseImpl.WilliamLilly
          }
        }

      }


    }


  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""discCenter":\s*true""".toRegex()))
    assertTrue(raw.contains(""""refraction":\s*false""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))

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
