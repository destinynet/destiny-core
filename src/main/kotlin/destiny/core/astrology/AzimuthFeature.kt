/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.serializers.astrology.StarSerializer
import jakarta.inject.Named
import kotlinx.serialization.Serializable

@Serializable
data class AzimuthConfig(@Serializable(with = StarSerializer::class)
                         val star: Star = Planet.SUN,
                         val coordinate: Coordinate = Coordinate.ECLIPTIC,
                         val geoAlt: Double = 0.0,
                         val temperature: Double = 0.0,
                         val pressure: Double = 1013.25,
                         val starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN
                         ): java.io.Serializable

@DestinyMarker
class AzimuthConfigBuilder : Builder<AzimuthConfig> {

  var star: Star = Planet.SUN
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var geoAlt: Double = 0.0
  var temperature: Double = 0.0
  var pressure: Double = 1013.25
  var starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN

  override fun build(): AzimuthConfig {
    return AzimuthConfig(star, coordinate, geoAlt, temperature, pressure, starTypeOptions)
  }

  companion object {
    fun azimuth(block :AzimuthConfigBuilder.() -> Unit = {} ) : AzimuthConfig {
      return AzimuthConfigBuilder().apply(block).build()
    }
  }
}


@Named
class AzimuthFeature(val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator) : AbstractCachedFeature<AzimuthConfig, IStarPositionWithAzimuth>() {
  override val key: String = "azimuth"

  override val defaultConfig: AzimuthConfig = AzimuthConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: AzimuthConfig): IStarPositionWithAzimuth {
    return starPositionWithAzimuthImpl.getPositionWithAzimuth(config.star, gmtJulDay, loc, Centric.GEO, config.coordinate, config.temperature, config.pressure, config.starTypeOptions)
  }
}
