/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.fengshui.sanyuan

import com.jayway.jsonpath.JsonPath
import destiny.core.AbstractConfigTest
import destiny.core.Scale
import destiny.core.calendar.eightwords.*
import kotlinx.serialization.KSerializer
import kotlin.test.assertEquals

internal class NineStarConfigTest : AbstractConfigTest<NineStarConfig>() {

  override val serializer: KSerializer<NineStarConfig> = NineStarConfig.serializer()

  override val configByConstructor: NineStarConfig = NineStarConfig(
    listOf(Scale.YEAR, Scale.DAY),
    EightWordsConfig(
      yearMonthConfig = YearMonthConfig(
        YearConfig(270.0),
        MonthConfig(
          southernHemisphereOpposition = true,
          hemisphereBy = HemisphereBy.DECLINATION,
          monthImpl = MonthImpl.SunSign
        )
      ),
      dayHourConfig = DayHourConfig(
        DayConfig(changeDay = ChangeDay.ZI_MIDDLE, midnight = MidnightImpl.CLOCK0),
        HourBranchConfig(hourImpl = HourImpl.LMT)
      )
    )
  )

  override val assertion: (String) -> Unit = { raw: String ->
    assertEquals(listOf("YEAR", "DAY"), JsonPath.read<List<String>>(raw, "$.scales"))
    assertEquals(270.0, JsonPath.read<Double>(raw, "$.ewConfig.yearMonthConfig.yearConfig.changeYearDegree"))
    assertEquals(true, JsonPath.read<Boolean>(raw, "$.ewConfig.yearMonthConfig.monthConfig.southernHemisphereOpposition"))
    assertEquals("DECLINATION", JsonPath.read<String>(raw, "$.ewConfig.yearMonthConfig.monthConfig.hemisphereBy"))
    assertEquals("SunSign", JsonPath.read<String>(raw, "$.ewConfig.yearMonthConfig.monthConfig.monthImpl"))
    assertEquals("ZI_MIDDLE", JsonPath.read<String>(raw, "$.ewConfig.dayHourConfig.dayConfig.changeDay"))
    assertEquals("CLOCK0", JsonPath.read<String>(raw, "$.ewConfig.dayHourConfig.dayConfig.midnight"))
    assertEquals("LMT", JsonPath.read<String>(raw, "$.ewConfig.dayHourConfig.hourBranchConfig.hourImpl"))
  }
}
