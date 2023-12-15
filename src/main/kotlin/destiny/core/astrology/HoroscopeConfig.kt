package destiny.core.astrology

import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.core.calendar.GmtJulDay
import destiny.tools.Builder
import destiny.tools.DestinyMarker
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

  val horoscopeConfig: HoroscopeConfig
    get() = HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure, vocImpl, place)
}


@Serializable
data class HoroscopeConfig(
  override var points: Set<@Serializable(with = AstroPointSerializer::class) AstroPoint> = setOf(*Planet.values, *LunarNode.values, Axis.RISING, Axis.MERIDIAN),
  override var houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override var coordinate: Coordinate = Coordinate.ECLIPTIC,
  override var centric: Centric = Centric.GEO,
  override var temperature: Double = 0.0,
  override var pressure: Double = 1013.25,
  override var vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval,
  override var place: String? = null
) : IHoroscopeConfig


@DestinyMarker
class HoroscopeConfigBuilder : Builder<HoroscopeConfig> {
  var points: Set<AstroPoint> = setOf(*Planet.values, *LunarNode.values)
  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var centric: Centric = Centric.GEO
  var temperature: Double = 0.0
  var pressure: Double = 1013.25
  var vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval
  var place: String? = null

  override fun build(): HoroscopeConfig {
    return HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure, vocImpl, place)
  }

  companion object {
    fun horoscope(block: HoroscopeConfigBuilder.() -> Unit = {}): HoroscopeConfig {
      return HoroscopeConfigBuilder().apply(block).build()
    }
  }
}


interface IHoroscopeClassicalConfig : IHoroscopeConfig {
  var factories: List<IPlanetPatternFactory>
  val classicalConfig: HoroscopeClassicalConfig
    get() = HoroscopeClassicalConfig(this, factories)
}

data class HoroscopeClassicalConfig(
  val horoConfig: IHoroscopeConfig = HoroscopeConfig(),
  override var factories: List<IPlanetPatternFactory>
) : IHoroscopeClassicalConfig, IHoroscopeConfig by horoConfig

interface IHoroscopePresentConfig : IHoroscopeClassicalConfig {
  var viewGmt: GmtJulDay
}

// TODO : DSL
data class HoroscopePresentConfig(
  val horoscopeClassicConfig: HoroscopeClassicalConfig,
  override var viewGmt: GmtJulDay = GmtJulDay.now()
) : IHoroscopePresentConfig, IHoroscopeClassicalConfig by horoscopeClassicConfig
