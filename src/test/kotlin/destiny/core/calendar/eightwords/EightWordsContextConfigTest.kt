/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.calendar.eightwords

import destiny.core.AbstractConfigTest
import destiny.core.astrology.*
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
            DayConfig(changeDay = ChangeDay.ZI_MIDDLE , MidnightImpl.CLOCK0),
            HourBranchConfig(HourImpl.LMT, TransConfig(true, false, 23.0, 1000.0))
          )
        ),
        RisingSignConfig(
          HouseConfig(HouseSystem.EQUAL , Coordinate.SIDEREAL),
          TradChineseRisingSignConfig(HourImpl.LMT),
          RisingSignImpl.TradChinese
        ),
        ZodiacSignConfig(Planet.SUN, StarTypeOptions.PRECISE)
      )
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
                  "changeDay":"ZI_MIDDLE",
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
         }
      }
    """.trimIndent())
    assertEquals(expected, actual)
  }
}
