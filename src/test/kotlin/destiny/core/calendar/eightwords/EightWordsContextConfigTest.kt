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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.assertEquals

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
        ZodiacSignConfig(Planet.SUN, StarTypeOptions.PRECISE),
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
            zodiacSign {
              star = Planet.SUN
              starTypeOptions = StarTypeOptions.PRECISE
            }
          }
        }
      }
    }

  override val assertion = { raw: String ->

    val actual = Json.decodeFromString<JsonElement>(raw)
    val expected = Json.decodeFromString<JsonElement>("""
      {
         "eightWordsConfig":{
            "yearMonthConfig":{
               "yearConfig":{
                  "changeYearDegree":270.0
               },
               "monthConfig":{
                  "southernHemisphereOpposition":true,
                  "hemisphereBy":"DECLINATION",
                  "monthImpl":"SunSign"
               }
            },
            "dayHourConfig":{
               "dayConfig":{
                  "changeDayAfterZi":false,
                  "midnight":"CLOCK0"
               },
               "hourBranchConfig":{
                  "hourImpl":"LMT",
                  "transConfig":{
                     "discCenter":true,
                     "refraction":false,
                     "temperature":23.0,
                     "pressure":1000.0,
                     "starTypeOptions":{
                        "nodeType":"MEAN",
                        "apsisType":"MEAN"
                     }
                  }
               }
            }
         },
         "risingSignConfig":{
            "houseConfig":{
               "houseSystem":"EQUAL",
               "coordinate":"SIDEREAL"
            },
            "tradChineseRisingSignConfig":{
               "hourImpl":"LMT"
            },
            "risingSignImpl":"TradChinese"
         },
         "zodiacSignConfig":{
            "star":"Planet.SUN",
            "starTypeOptions":{
               "nodeType":"TRUE",
               "apsisType":"OSCU"
            }
         },
         "place":"台北市"
      }
    """.trimIndent())
    assertEquals(expected, actual)
  }
}
