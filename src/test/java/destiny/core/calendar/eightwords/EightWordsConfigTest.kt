/**
 * Created by smallufo on 2021-08-11.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.calendar.eightwords.EightWordsConfigBuilder.Companion.ewConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class EightWordsConfigTest : AbstractConfigTest<EightWordsConfig>() {

  override val serializer: KSerializer<EightWordsConfig> =  EightWordsConfig.serializer()

  override val configByConstructor: EightWordsConfig = EightWordsConfig(
    yearMonthConfig = YearMonthConfig(
      YearConfig(270.0),
      MonthConfig(
        southernHemisphereOpposition = true,
        hemisphereBy = HemisphereBy.DECLINATION,
        monthImpl = MonthImpl.SunSign
      )
    ),
    dayHourConfig = DayHourConfig(
      DayConfig(changeDayAfterZi = false , midnight = MidnightImpl.CLOCK0),
      HourBranchConfig(hourImpl = HourImpl.LMT)
    )
  )

  override val configByFunction: EightWordsConfig = ewConfig {
    yearMonth {
      year {
        changeYearDegree = 270.0
      }
      month {
        southernHemisphereOpposition = true
        hemisphereBy = HemisphereBy.DECLINATION
        monthImpl = MonthImpl.SunSign
      }
    }

    dayHour {
      day {
        changeDayAfterZi = false
        midnight = MidnightImpl.CLOCK0
      }
      hourBranch {
        hourImpl = HourImpl.LMT
      }
    }
  }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
  }
}
