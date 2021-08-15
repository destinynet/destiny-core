/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.astrology.HoroscopeConfigBuilder.Companion.horoscope
import destiny.core.astrology.classical.VoidCourseConfig
import kotlinx.serialization.KSerializer
import kotlin.test.assertTrue

internal class HoroscopeConfigTest : AbstractConfigTest<HoroscopeConfig>() {
  override val serializer: KSerializer<HoroscopeConfig> = HoroscopeConfig.serializer()

  override val configByConstructor: HoroscopeConfig = HoroscopeConfig(
    points = setOf(Planet.MOON, Asteroid.CERES, FixedStar.ALGOL, Hamburger.ADMETOS),
    houseSystem = HouseSystem.MERIDIAN,
    coordinate = Coordinate.EQUATORIAL,
    centric = Centric.TOPO,
    temperature = 23.0,
    pressure = 1000.0,
    vocImpl = VoidCourseConfig.VoidCourseImpl.Hellenistic,
    place = "台北市"
  )

  override val configByFunction: HoroscopeConfig = horoscope {
    points = setOf(Planet.MOON, Asteroid.CERES, FixedStar.ALGOL, Hamburger.ADMETOS)
    houseSystem = HouseSystem.MERIDIAN
    coordinate = Coordinate.EQUATORIAL
    centric = Centric.TOPO
    temperature = 23.0
    pressure = 1000.0
    vocImpl = VoidCourseConfig.VoidCourseImpl.Hellenistic
    place = "台北市"
  }

  override val assertion: (String) -> Unit = { raw: String ->
    logger.info { raw }
    assertTrue(raw.contains("Planet.MOON"))
    assertTrue(raw.contains("Asteroid.CERES"))
    assertTrue(raw.contains("Fixed.ALGOL"))
    assertTrue(raw.contains("Hamburger.ADMETOS"))

    assertTrue(raw.contains(""""houseSystem":\s*"MERIDIAN"""".toRegex()))
    assertTrue(raw.contains(""""coordinate":\s*"EQUATORIAL"""".toRegex()))
    assertTrue(raw.contains(""""centric":\s*"TOPO"""".toRegex()))
    assertTrue(raw.contains(""""temperature":\s*23.0""".toRegex()))
    assertTrue(raw.contains(""""pressure":\s*1000.0""".toRegex()))
    assertTrue(raw.contains(""""vocImpl":\s*"Hellenistic"""".toRegex()))
    assertTrue(raw.contains(""""place":\s*"台北市"""".toRegex()))
  }
}
