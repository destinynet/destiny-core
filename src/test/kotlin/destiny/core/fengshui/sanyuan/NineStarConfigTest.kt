/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.AbstractConfigTest
import destiny.core.Scale
import destiny.core.calendar.eightwords.*
import destiny.core.fengshui.sanyuan.NineStarConfigBuilder.Companion.nineStarConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

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
        DayConfig(changeDayAfterZi = false, midnight = MidnightImpl.CLOCK0),
        HourBranchConfig(hourImpl = HourImpl.LMT)
      )
    )
  )

  override val configByFunction: NineStarConfig
    get() {
      return with(EightWordsConfig()) {
        nineStarConfig {
          scales = listOf(Scale.YEAR, Scale.DAY)

          changeYearDegree = 270.0
          southernHemisphereOpposition = true
          hemisphereBy = HemisphereBy.DECLINATION
          monthImpl = MonthImpl.SunSign
          changeDayAfterZi = false
          midnight = MidnightImpl.CLOCK0
          hourImpl = HourImpl.LMT
        }
      }
    }
  override val assertion: (String) -> Unit = { raw: String ->
    assertTrue { raw.contains("YEAR") }
    assertTrue { raw.contains("DAY") }
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
  }
}
