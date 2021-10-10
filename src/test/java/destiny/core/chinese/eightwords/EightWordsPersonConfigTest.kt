/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNoteImpl
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
          HourBranchConfig(HourImpl.LMT)
        )
      ),
      RisingSignConfig(
        HouseConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL),
        TradChineseRisingSignConfig(HourImpl.LMT),
        RisingSignConfig.Impl.TradChinese
      ),
      ZodiacSignConfig(Planet.SUN),
      HouseConfig(HouseSystem.EQUAL, Coordinate.SIDEREAL),
      "台北市"
    ),
    fortuneLargeConfig = FortuneLargeConfig(FortuneLargeConfig.Impl.SolarTermsSpan, 90.0),
    fortuneSmallConfig = FortuneSmallConfig(FortuneSmallConfig.Impl.SixGia, 90, intAgeNotes = listOf(IntAgeNoteImpl.Minguo))
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
    fortuneLarge {
      impl = FortuneLargeConfig.Impl.SolarTermsSpan
      span = 90.0
    }
    fortuneSmall {
      impl = FortuneSmallConfig.Impl.SixGia
      count = 90
      intAgeNotes(listOf(IntAgeNoteImpl.Minguo))
    }
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

    assertTrue(raw.contains(""""impl":\s*"SolarTermsSpan"""".toRegex()))
    assertTrue(raw.contains(""""span":\s*90.0""".toRegex()))


    assertTrue(raw.contains(""""impl":\s*"SixGia"""".toRegex()))
    assertTrue(raw.contains(""""count":\s*90""".toRegex()))
    assertTrue(raw.contains("""\[\s*"Minguo"\s*]""".toRegex()))


  }
}
