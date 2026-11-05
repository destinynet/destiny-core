/**
 * Created by smallufo on 2022-02-13.
 */
package destiny.core.calendar

import com.jayway.jsonpath.JsonPath
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
import kotlin.test.assertEquals

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
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig.transConfig.pressure"))
    assertEquals("YEAR_LUNAR", JsonPath.read<String>(raw, "$.lunarStationConfig.yearType"))
    assertEquals("AnimalExplained", JsonPath.read<String>(raw, "$.lunarStationConfig.monthlyImpl"))
    assertEquals("Fixed", JsonPath.read<String>(raw, "$.lunarStationConfig.hourlyImpl"))
    assertEquals("Planet.VENUS", JsonPath.read<String>(raw, "$.vocConfig.planet"))
    assertEquals("TOPO", JsonPath.read<String>(raw, "$.vocConfig.centric"))
    assertEquals("WilliamLilly", JsonPath.read<String>(raw, "$.vocConfig.vocImpl"))
  }
}
