/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable


@Serializable
data class HoroscopeConfig(
  val points: Set<@Serializable(with = PointSerializer::class) Point> = setOf(*Planet.array),
  val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  val coordinate: Coordinate = Coordinate.ECLIPTIC,
  val centric: Centric = Centric.GEO,
  val temperature: Double = 0.0,
  val pressure: Double = 1013.25
)

@DestinyMarker
class HoroscopeConfigBuilder : Builder<HoroscopeConfig> {
  var points: Set<Point> = setOf(*Planet.array)
  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var centric: Centric = Centric.GEO
  var temperature: Double = 0.0
  var pressure: Double = 1013.25

  override fun build(): HoroscopeConfig {
    return HoroscopeConfig(points, houseSystem, coordinate, centric, temperature, pressure)
  }

  companion object {
    fun horoscope(block : HoroscopeConfigBuilder.() -> Unit = {}) : HoroscopeConfig {
      return HoroscopeConfigBuilder().apply(block).build()
    }
  }
}

class HoroscopeFeature {
}
