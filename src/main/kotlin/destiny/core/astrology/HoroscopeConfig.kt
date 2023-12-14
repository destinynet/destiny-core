package destiny.core.astrology

import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.core.calendar.GmtJulDay
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.serializers.AstroPointSerializer
import kotlinx.serialization.Serializable
import java.util.*

interface IHoroscopeConfig : java.io.Serializable {
  val points: Set<@Serializable(with = AstroPointSerializer::class) AstroPoint>
  val houseSystem: HouseSystem
  val coordinate: Coordinate
  val centric: Centric
  val temperature: Double
  val pressure: Double
  val vocImpl: VoidCourseImpl
  val place: String?
}


@Serializable
data class HoroscopeConfig(
  override val points: Set<@Serializable(with = AstroPointSerializer::class) AstroPoint> = setOf(*Planet.values, *LunarNode.values, Axis.RISING, Axis.MERIDIAN),
  override val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override val coordinate: Coordinate = Coordinate.ECLIPTIC,
  override val centric: Centric = Centric.GEO,
  override val temperature: Double = 0.0,
  override val pressure: Double = 1013.25,
  override val vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval,
  override val place: String? = null
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
  val locale: Locale
  val factories: List<IPlanetPatternFactory>
}

data class HoroscopeClassicalConfig(
  override val locale: Locale = Locale.getDefault(),
  val horoConfig: IHoroscopeConfig = HoroscopeConfig(),
  override val factories: List<IPlanetPatternFactory>
) : IHoroscopeClassicalConfig, IHoroscopeConfig by horoConfig

interface IHoroscopePresentConfig : IHoroscopeClassicalConfig {
  var viewGmt: GmtJulDay
}
// TODO : DSL
data class HoroscopePresentConfig(
  val horoscopeConfigConfig: HoroscopeClassicalConfig ,
  override var viewGmt: GmtJulDay = GmtJulDay.now()
) : IHoroscopePresentConfig, IHoroscopeClassicalConfig by horoscopeConfigConfig
