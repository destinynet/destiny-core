/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.IntAgeNote
import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.Planet
import destiny.core.astrology.TransConfigBuilder
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.eightwords.PersonConfigBuilder.Companion.ewPersonConfig
import kotlinx.serialization.KSerializer
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EightWordsPersonConfigTest : AbstractConfigTest<EightWordsPersonConfig>() {

  override val serializer: KSerializer<EightWordsPersonConfig> = EightWordsPersonConfig.serializer()

  override val configByConstructor: EightWordsPersonConfig? = null

  override val configByFunction: EightWordsPersonConfig
    get() {

      val yearConfig = YearConfigBuilder.yearConfig {
        changeYearDegree = 270.0
      }
      val monthConfig = MonthConfigBuilder.monthConfig {
        southernHemisphereOpposition = true
        hemisphereBy = HemisphereBy.DECLINATION
        monthImpl = MonthImpl.SunSign
      }

      val yearMonthConfig = with(yearConfig) {
        with(monthConfig) {
          YearMonthConfigBuilder.yearMonthConfig {
          }
        }
      }


      val transConfig = TransConfigBuilder.trans {
        discCenter = true
        refraction = false
        temperature = 23.0
        pressure = 1000.0
      }

      val hourBranchConfig = with(transConfig) {
        HourBranchConfigBuilder.hourBranchConfig {
          hourImpl = HourImpl.LMT
        }
      }

      val dayConfig = DayConfigBuilder.dayConfig {
        changeDayAfterZi = false
        midnight = MidnightImpl.CLOCK0
      }


      val dayHourConfig = with(dayConfig) {
        with(hourBranchConfig) {
          DayHourConfigBuilder.dayHour {
          }
        }
      }

      val ewConfig = with(yearMonthConfig) {
        with(dayHourConfig) {
          EightWordsConfigBuilder.ewConfig {
          }
        }
      }

      val ewContextConfig = with(ewConfig) {
        EightWordsContextConfigBuilder.ewContext {
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
      }


      return with(ewContextConfig) {
        ewPersonConfig {
          fortuneLarge {
            impl = FortuneLargeImpl.SolarTermsSpan
            span = 90.0
          }
          fortuneSmall {
            impl = FortuneSmallConfig.Impl.SixGia
            count = 90
            intAgeNotes(listOf(IntAgeNote.Minguo))
          }
          ewContextScore = EwContextScore.OctaDivide46
          locale = Locale.JAPANESE
        }
      }

    }

  override val assertion: (String) -> Unit = { raw ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertFalse(raw.contains(""""changeYearDegree":\s*315.0""".toRegex()))

    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertFalse(raw.contains(""""southernHemisphereOpposition":\s*false""".toRegex()))

    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertFalse(raw.contains(""""hemisphereBy":\s*"EQUATOR"""".toRegex()))

    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))
    assertFalse(raw.contains(""""monthImpl":\s*"SolarTerms"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertFalse(raw.contains(""""changeDayAfterZi":\s*true""".toRegex()))

    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertFalse(raw.contains(""""midnight":\s*"NADIR"""".toRegex()))

    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))
    assertFalse(raw.contains(""""hourImpl":\s*"TST"""".toRegex()))

    assertTrue(raw.contains(""""tradChineseRisingSignConfig""".toRegex()))
    assertTrue(raw.contains(""""place":\s*"台北市"""".toRegex()))

    assertTrue(raw.contains(""""impl":\s*"SolarTermsSpan"""".toRegex()))
    assertTrue(raw.contains(""""span":\s*90.0""".toRegex()))


    assertTrue(raw.contains(""""impl":\s*"SixGia"""".toRegex()))
    assertTrue(raw.contains(""""count":\s*90""".toRegex()))
    assertTrue(raw.contains("""\[\s*"Minguo"\s*]""".toRegex()))

    assertTrue(raw.contains(""""ewContextScore":\s*"OctaDivide46"""".toRegex()))
    assertTrue(raw.contains(""""locale":\s*"ja"""".toRegex()))

  }
}
