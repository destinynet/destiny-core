/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.*
import destiny.core.calendar.eightwords.EightWordsContextConfigBuilder.Companion.ewContext
import destiny.core.calendar.eightwords.MonthConfigBuilder.Companion.monthConfig
import destiny.core.calendar.eightwords.RisingSignConfigBuilder.Companion.risingSign
import destiny.core.calendar.eightwords.YearConfigBuilder.Companion.yearConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EightWordsContextConfigTest : AbstractConfigTest<EightWordsContextConfig>() {
  override val serializer: KSerializer<EightWordsContextConfig> = EightWordsContextConfig.serializer()

  override val configByConstructor: EightWordsContextConfig
    get() {
      return EightWordsContextConfig(
        EightWordsConfig(
          YearMonthConfig(
            YearConfig(270.0),
            MonthConfig(true, HemisphereBy.DECLINATION, MonthImpl.SunSign)
          ),
          DayHourConfig(
            DayConfig(changeDayAfterZi = false , MidnightImpl.CLOCK0),
            HourBranchConfig(HourImpl.LMT, TransConfig(true, false, 23.0, 1000.0))
          )
        ),
        RisingSignConfig(
          HouseConfig(HouseSystem.EQUAL , Coordinate.SIDEREAL),
          TradChineseRisingSignConfig(HourImpl.LMT),
          RisingSignImpl.TradChinese
        ),
        ZodiacSignConfig(Planet.SUN),
        "台北市"
      )
    }

  override val configByFunction: EightWordsContextConfig
    get() {

      val yearConfig = yearConfig {
        changeYearDegree = 270.0
      }
      val monthConfig = monthConfig {
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

      val risingSignConfig = with(HouseConfig()) {
        houseSystem = HouseSystem.EQUAL
        coordinate = Coordinate.SIDEREAL
        risingSign {
          tradChinese {
            hourImpl = HourImpl.LMT
          }
        }
      }

      return with(ewConfig) {
        with(risingSignConfig) {
          ewContext {
            place = "台北市"
          }
        }
      }
    }

  override val assertion = { raw: String ->
    assertTrue(raw.contains(""""changeYearDegree":\s*270.0""".toRegex()))
    assertTrue(raw.contains(""""southernHemisphereOpposition":\s*true""".toRegex()))
    assertFalse(raw.contains(""""southernHemisphereOpposition":\s*false""".toRegex()))

    assertTrue(raw.contains(""""hemisphereBy":\s*"DECLINATION"""".toRegex()))
    assertTrue(raw.contains(""""monthImpl":\s*"SunSign"""".toRegex()))

    assertTrue(raw.contains(""""changeDayAfterZi":\s*false""".toRegex()))
    assertFalse(raw.contains(""""changeDayAfterZi":\s*true""".toRegex()))

    assertTrue(raw.contains(""""midnight":\s*"CLOCK0"""".toRegex()))
    assertTrue(raw.contains(""""hourImpl":\s*"LMT"""".toRegex()))

    assertTrue(raw.contains(""""tradChineseRisingSignConfig""".toRegex()))

    assertTrue(raw.contains(""""place":\s*"台北市"""".toRegex()))
  }
}
