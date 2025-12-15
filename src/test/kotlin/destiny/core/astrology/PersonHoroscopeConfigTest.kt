/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.HoroscopeConfigBuilder.Companion.horoscope
import destiny.core.astrology.PersonHoroscopeConfigBuilder.Companion.personHoroscope
import destiny.core.astrology.classical.VoidCourseImpl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class PersonHoroscopeConfigTest : AbstractConfigTest<PersonHoroscopeConfig>() {

  override val serializer: KSerializer<PersonHoroscopeConfig> = PersonHoroscopeConfig.serializer()

  override val configByConstructor: PersonHoroscopeConfig = PersonHoroscopeConfig(
    HoroscopeConfig(
      points = setOf(Planet.MOON, Asteroid.CERES, FixedStar.ALGOL, Hamburger.ADMETOS),
      houseSystem = HouseSystem.MERIDIAN,
      coordinate = Coordinate.EQUATORIAL,
      centric = Centric.TOPO,
      temperature = 23.0,
      pressure = 1000.0,
      vocImpl = VoidCourseImpl.Hellenistic,
      place = "台北市",
      relocations = mapOf(
        Planet.MOON to 60.0,
        Asteroid.CERES to 120.0
      ),
      starTypeOptions = StarTypeOptions.PRECISE
    )
  )

  override val configByFunction: PersonHoroscopeConfig
    get() {
      val horoscopeConfig = horoscope {
        points = setOf(Planet.MOON, Asteroid.CERES, FixedStar.ALGOL, Hamburger.ADMETOS)
        houseSystem = HouseSystem.MERIDIAN
        coordinate = Coordinate.EQUATORIAL
        centric = Centric.TOPO
        temperature = 23.0
        pressure = 1000.0
        vocImpl = VoidCourseImpl.Hellenistic
        place = "台北市"
        relocations = mapOf(
          Planet.MOON to 60.0,
          Asteroid.CERES to 120.0
        )
        starTypeOptions = StarTypeOptions.PRECISE
      }
      return with(horoscopeConfig) {
        personHoroscope {
        }
      }
    }

  override val assertion: (String) -> Unit = { raw: String ->
    val actual = Json.decodeFromString<JsonElement>(raw)

    logger.info { "actual = $actual" }

    val expected = Json.decodeFromString<JsonElement>("""
      {
         "horoscopeConfig":{
            "points":[
               "Planet.MOON",
               "Asteroid.CERES",
               "FixedStar.ALGOL",
               "Hamburger.ADMETOS"
            ],
            "houseSystem":"MERIDIAN",
            "coordinate":"EQUATORIAL",
            "centric":"TOPO",
            "temperature":23.0,
            "pressure":1000.0,
            "vocImpl":"Hellenistic",
            "place":"台北市",
            "relocations":{
               "Planet.MOON":60.0,
               "Asteroid.CERES":120.0
            },
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
