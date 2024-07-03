/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.HoroscopeConfigBuilder.Companion.horoscope
import destiny.core.astrology.classical.VoidCourseImpl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.assertEquals

internal class HoroscopeConfigTest : AbstractConfigTest<HoroscopeConfig>() {
  override val serializer: KSerializer<HoroscopeConfig> = HoroscopeConfig.serializer()

  override val configByConstructor: HoroscopeConfig = HoroscopeConfig(
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
    )
  )

  override val configByFunction: HoroscopeConfig = horoscope {
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
  }

  override val assertion: (String) -> Unit = { raw: String ->
    logger.info { raw }

    val actual = Json.decodeFromString<JsonElement>(raw)
    val expected = Json.decodeFromString<JsonElement>("""
      {
          "points": [
              "Planet.MOON",
              "Asteroid.CERES",
              "Fixed.ALGOL",
              "Hamburger.ADMETOS"
          ],
          "houseSystem": "MERIDIAN",
          "coordinate": "EQUATORIAL",
          "centric": "TOPO",
          "temperature": 23.0,
          "pressure": 1000.0,
          "vocImpl": "Hellenistic",
          "place": "台北市",
          "relocations": {
              "Planet.MOON": 60.0,
              "Asteroid.CERES": 120.0
          }
      }
    """.trimIndent())
    assertEquals(expected, actual)
  }
}
