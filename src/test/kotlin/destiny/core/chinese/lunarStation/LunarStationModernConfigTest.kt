/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.lunarStation

import com.jayway.jsonpath.JsonPath
import destiny.core.calendar.eightwords.ChangeDay
import destiny.core.AbstractConfigTest
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.LunarStationConfigBuilder.Companion.lunarStation
import destiny.core.chinese.lunarStation.LunarStationModernConfigBuilder.Companion.lunarStationModern
import kotlinx.serialization.KSerializer
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class LunarStationModernConfigTest : AbstractConfigTest<LunarStationModernConfig>() {
  override val serializer: KSerializer<LunarStationModernConfig> = LunarStationModernConfig.serializer()

  override val configByConstructor: LunarStationModernConfig? = null
//    LunarStationModernConfig(
//    method = IModernContextModel.Method.SPECIFIED,
//    specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0)),
//    description = "test123"
//  )

  override val configByFunction: LunarStationModernConfig
    get() {

      val lsConfig = with(EightWordsConfig()) {
        lunarStation {
          discCenter = true
          refraction = false

          yearType = YearType.YEAR_LUNAR
          yearEpoch = YearEpoch.EPOCH_1864

          changeDay = ChangeDay.ZI_MIDDLE
          monthlyImpl = MonthlyImpl.AnimalExplained
          hourlyImpl = HourlyImpl.Fixed
        }
      }


      return with(lsConfig) {
        lunarStationModern {
          method = IModernContextModel.Method.SPECIFIED
          specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0))
          description = "test123"
        }
      }
    }

  override val assertion: (String) -> Unit = { raw ->
    logger.info { raw }

    assertEquals("YEAR_LUNAR", JsonPath.read<String>(raw, "$.lunarStationConfig.yearType"))
    assertEquals("EPOCH_1864", JsonPath.read<String>(raw, "$.lunarStationConfig.yearEpoch"))
    assertEquals("SPECIFIED", JsonPath.read<String>(raw, "$.method"))
    assertEquals(2459449.0, JsonPath.read<Double>(raw, "$.specifiedGmtJulDay.julDay"))
    assertEquals("test123", JsonPath.read<String>(raw, "$.description"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig.transConfig.refraction"))
  }
}
