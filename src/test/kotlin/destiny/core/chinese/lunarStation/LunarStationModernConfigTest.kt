/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.lunarStation

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.TransConfig
import destiny.core.calendar.eightwords.ChangeDay
import destiny.core.AbstractConfigTest
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.DayConfig
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.chinese.YearType
import kotlinx.serialization.KSerializer
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class LunarStationModernConfigTest : AbstractConfigTest<LunarStationModernConfig>() {
  override val serializer: KSerializer<LunarStationModernConfig> = LunarStationModernConfig.serializer()

  override val configByConstructor: LunarStationModernConfig = LunarStationModernConfig(
    LunarStationConfig(
      yearType = YearType.YEAR_LUNAR,
      yearEpoch = YearEpoch.EPOCH_1864,
      monthlyImpl = MonthlyImpl.AnimalExplained,
      hourlyImpl = HourlyImpl.Fixed,
      ewConfig = EightWordsConfig(
        dayHourConfig = DayHourConfig(
          DayConfig(changeDay = ChangeDay.ZI_MIDDLE),
          HourBranchConfig(transConfig = TransConfig(discCenter = true, refraction = false))
        )
      )
    ),
    method = IModernContextModel.Method.SPECIFIED,
    specifiedGmtJulDay = TimeTools.getGmtJulDay(LocalDateTime.of(2021, 8, 22, 12, 0)),
    description = "test123"
  )

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
