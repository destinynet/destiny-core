/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.astrology.TransConfig
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.chinese.YearType
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class LunarStationConfigTest : AbstractConfigTest<LunarStationConfig>() {

  override val serializer: KSerializer<LunarStationConfig> = LunarStationConfig.serializer()

  override val configByConstructor: LunarStationConfig = LunarStationConfig(
    yearType = YearType.YEAR_LUNAR,
    yearEpoch = YearEpoch.EPOCH_1864,
    monthlyImpl = MonthlyImpl.AnimalExplained,
    monthAlgo = MonthAlgo.MONTH_FIXED_THIS,
    hourlyImpl = HourlyImpl.Fixed,
    ewConfig = EightWordsConfig(
      dayHourConfig = DayHourConfig(
        hourBranchConfig = HourBranchConfig(
          transConfig = TransConfig(
            discCenter = true,
            refraction = false,
            temperature = 23.0,
            pressure = 1000.0
          )
        )
      )
    )
  )

  override val assertion: (String) -> Unit = { raw ->
    assertEquals("YEAR_LUNAR", JsonPath.read<String>(raw, "$.yearType"))
    assertEquals("EPOCH_1864", JsonPath.read<String>(raw, "$.yearEpoch"))
    assertEquals("AnimalExplained", JsonPath.read<String>(raw, "$.monthlyImpl"))
    assertEquals("MONTH_FIXED_THIS", JsonPath.read<String>(raw, "$.monthAlgo"))
    assertEquals("Fixed", JsonPath.read<String>(raw, "$.hourlyImpl"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.ewConfig.dayHourConfig.hourBranchConfig.transConfig.discCenter"))
    assertEquals(false, JsonPath.read<Boolean>(raw, "$.ewConfig.dayHourConfig.hourBranchConfig.transConfig.refraction"))
    assertEquals(23.0, JsonPath.read<Double>(raw, "$.ewConfig.dayHourConfig.hourBranchConfig.transConfig.temperature"))
    assertEquals(1000.0, JsonPath.read<Double>(raw, "$.ewConfig.dayHourConfig.hourBranchConfig.transConfig.pressure"))
  }

}
