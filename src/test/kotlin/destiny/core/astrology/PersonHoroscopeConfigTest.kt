/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology

import destiny.core.AbstractConfigTest
import destiny.core.Gender
import destiny.core.astrology.HoroscopeConfigBuilder.Companion.horoscope
import destiny.core.astrology.PersonHoroscopeConfigBuilder.Companion.personHoroscope
import destiny.core.astrology.classical.VoidCourseImpl
import kotlinx.serialization.KSerializer
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
      place = "台北市"
    ), Gender.女, "小明"
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
      }
      return with(horoscopeConfig) {
        personHoroscope {
          gender = Gender.女
          name = "小明"
        }
      }
    }

  override val assertion: (String) -> Unit = { raw: String ->
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

    assertTrue(raw.contains(""""gender":\s*"女"""".toRegex()))
    assertTrue(raw.contains(""""name":\s*"小明"""".toRegex()))


  }

}
