/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.*
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.eightwords.PersonConfigBuilder.Companion.ewPersonConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class EightWordsPersonConfigTest : AbstractConfigTest<EightWordsPersonConfig>() {

  override val serializer: KSerializer<EightWordsPersonConfig> = EightWordsPersonConfig.serializer()

  override val configByConstructor: EightWordsPersonConfig = EightWordsPersonConfig(
    eightwordsContextConfig = EightWordsContextConfig(
      EightWordsConfig(
        YearMonthConfig(
          YearConfig(270.0),
          MonthConfig(true , HemisphereBy.DECLINATION ,MonthConfig.MonthImpl.SunSign)
        ),
        DayHourConfig(
          DayConfig(changeDayAfterZi = false , DayConfig.MidnightImpl.CLOCK0),
          HourBranchConfig(HourBranchConfig.HourImpl.LMT)
        )
      ),
      RisingSignConfig(
        HouseConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL),
        TradChineseRisingSignConfig(HourBranchConfig.HourImpl.LMT),
        RisingSignConfig.Impl.TradChinese
      ),
      ZodiacSignConfig(Planet.SUN),
      HouseConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL),
      "台北市"
    ),
    fortuneLargeImpl = EightWordsPersonConfig.FortuneLarge.Span,
    fortuneSmallImpl = EightWordsPersonConfig.FortuneSmall.Star
  )

  override val configByFunction: EightWordsPersonConfig = ewPersonConfig {
    ewContextConfig {
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
            hourImpl = HourBranchConfig.HourImpl.LMT
          }
        }
      }
      risingSign {
        houseCusp {
          houseSystem = HouseSystem.EQUAL
          coordinate = Coordinate.SIDEREAL
        }
        tradChinese {
          hourImpl = HourBranchConfig.HourImpl.LMT
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

    fortuneLargeImpl = EightWordsPersonConfig.FortuneLarge.Span
    fortuneSmallImpl = EightWordsPersonConfig.FortuneSmall.Star
  }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))
    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
    assertTrue(raw.contains(""""tradChineseRisingSignConfig""".toRegex()))
    assertTrue(raw.contains(""""place":\s*"台北市"""".toRegex()))

    assertTrue(raw.contains(""""fortuneLargeImpl":\s*"Span"""".toRegex()))
    assertTrue(raw.contains(""""fortuneSmallImpl":\s*"Star"""".toRegex()))


  }
}
