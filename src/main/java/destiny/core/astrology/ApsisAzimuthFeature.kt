/**
 * Created by smallufo on 2021-08-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class ApsisAzimuthConfig(@Serializable(with = PointSerializer::class)
                              val star: Star = Planet.SUN,
                              val coordinate: Coordinate = Coordinate.ECLIPTIC,
                              val nodeType: NodeType = NodeType.MEAN,
                              val temperature: Double = 0.0,
                              val pressure: Double = 1013.25)

@DestinyMarker
class ApsisAzimuthConfigBuilder : Builder<ApsisAzimuthConfig> {
  var star: Star = Planet.SUN
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var nodeType: NodeType = NodeType.MEAN
  var temperature: Double = 0.0
  var pressure: Double = 1013.25

  override fun build(): ApsisAzimuthConfig {
    return ApsisAzimuthConfig(star, coordinate, nodeType, temperature, pressure)
  }

  companion object {
    fun apsisAzimuth(block: ApsisAzimuthConfigBuilder.() -> Unit = {}): ApsisAzimuthConfig {
      return ApsisAzimuthConfigBuilder().apply(block).build()
    }
  }
}

class ApsisAzimuthFeature(private val impl: IApsisWithAzimuth) : Feature<ApsisAzimuthConfig, Map<Apsis, StarPosWithAzimuth>> {
  override val key: String = "apsisAzimuth"

  override val defaultConfig: ApsisAzimuthConfig = ApsisAzimuthConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: ApsisAzimuthConfig): Map<Apsis, StarPosWithAzimuth> {
    return impl.getPositionsWithAzimuths(gmtJulDay, loc, config)
  }
}
