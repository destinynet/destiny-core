package destiny.core.astrology

import destiny.core.Gender
import destiny.core.IPresentConfig
import destiny.core.astrology.classical.ClassicalConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.AstroPointSerializer
import kotlinx.serialization.Serializable

interface IHoroscopeConfig : java.io.Serializable {
  var points: Set<@Serializable(with = AstroPointSerializer::class) AstroPoint>
  var houseSystem: HouseSystem
  var coordinate: Coordinate
  var centric: Centric
  var temperature: Double
  var pressure: Double
  var vocImpl: VoidCourseImpl
  var place: String?
  val relocations : Map<AstroPoint, Double>

  val horoscopeConfig: HoroscopeConfig
    get() = HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure, vocImpl, place)
}

interface IClassicalConfig : java.io.Serializable {
  var factories: List<IPlanetPatternFactory>
  val classicalConfig: ClassicalConfig
    get() = ClassicalConfig(factories)
}

interface IPersonHoroscopeConfig : IHoroscopeConfig {
  var gender: Gender
  var name: String?
  val personHoroscopeConfig: PersonHoroscopeConfig
    get() {
      return PersonHoroscopeConfig(horoscopeConfig, gender, name)
    }
}

data class TransitConfig(val forward: Boolean = true) : java.io.Serializable

interface IHoroscopePresentConfig : IPersonHoroscopeConfig , IPresentConfig {
  val transitConfig: TransitConfig?
}

// TODO : DSL
data class HoroscopePresentConfig(
  val classicConfig: ClassicalConfig,
  val horoscopePersonConfig: PersonHoroscopeConfig,
  override var viewGmt: GmtJulDay = GmtJulDay.now(),
  override val transitConfig: TransitConfig?
) : IHoroscopePresentConfig, IClassicalConfig by classicConfig
    , IPersonHoroscopeConfig by horoscopePersonConfig
