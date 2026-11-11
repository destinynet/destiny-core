/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedFeature
import destiny.tools.JSerializable
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
                         ): JSerializable


@Named
class AzimuthFeature(val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator) : AbstractCachedFeature<AzimuthConfig, IStarPositionWithAzimuth>() {
  override val key: String = "azimuth"

  override val defaultConfig: AzimuthConfig = AzimuthConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: AzimuthConfig): IStarPositionWithAzimuth {
    return starPositionWithAzimuthImpl.getPositionWithAzimuth(config.star, gmtJulDay, loc, Centric.GEO, config.coordinate, config.temperature, config.pressure, config.starTypeOptions)
  }
}
