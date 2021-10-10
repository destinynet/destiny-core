/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.*
import destiny.core.calendar.eightwords.EightWordsContextConfigBuilder.Companion.ewContext
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class EightWordsContextConfigTest : AbstractConfigTest<EightWordsContextConfig>() {
  override val serializer: KSerializer<EightWordsContextConfig> = EightWordsContextConfig.serializer()

  override val configByConstructor: EightWordsContextConfig = EightWordsContextConfig(
    EightWordsConfig(
      YearMonthConfig(
        YearConfig(270.0),
        MonthConfig(true , HemisphereBy.DECLINATION ,MonthConfig.MonthImpl.SunSign)
      ),
      DayHourConfig(
        DayConfig(changeDayAfterZi = false , DayConfig.MidnightImpl.CLOCK0),
        HourBranchConfig(HourImpl.LMT)
      )
    ),
    RisingSignConfig(
      HouseConfig(HouseSystem.EQUAL , Coordinate.SIDEREAL),
      TradChineseRisingSignConfig(HourImpl.LMT),
      RisingSignConfig.Impl.TradChinese
    ),
    ZodiacSignConfig(Planet.SUN),
    HouseConfig(HouseSystem.EQUAL , Coordinate.SIDEREAL),
    "台北市"
  )

  override val configByFunction: EightWordsContextConfig = ewContext {
    ewConfig {
      yearMonth {
        year {
          changeYearDegree = 270.0
        }
        month {
          southernHemisphereOpposition = true
          hemisphereBy = HemisphereBy.DECLINATION
          monthImpl = MonthConfig.MonthImpl.SunSign
        }
      }

      dayHour {
        day {
          changeDayAfterZi = false
          midnight = DayConfig.MidnightImpl.CLOCK0
        }
        hourBranch {
          hourImpl = HourImpl.LMT
        }
      }
    }
    risingSign {
      houseCusp {
        houseSystem = HouseSystem.EQUAL
        coordinate = Coordinate.SIDEREAL
      }
      tradChinese {
        hourImpl = HourImpl.LMT
      }
    }
    zodiacSign {
      star = Planet.SUN
    }
    house {
      houseSystem = HouseSystem.EQUAL
      coordinate = Coordinate.SIDEREAL
    }
    place = "台北市"
  }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))

    assertTrue(raw.contains(""""tradChineseRisingSignConfig""".toRegex()))

    assertTrue(raw.contains(""""place":\s*"台北市"""".toRegex()))
  }
}
