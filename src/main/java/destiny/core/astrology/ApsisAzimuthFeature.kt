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
                              val star: Star = Planet.MOON,
                              val coordinate: Coordinate = Coordinate.ECLIPTIC,
                              val nodeType: NodeType = NodeType.MEAN,
                              val temperature: Double = 0.0,
                              val pressure: Double = 1013.25)

@DestinyMarker
class ApsisAzimuthConfigBuilder : Builder<ApsisAzimuthConfig> {
  var star: Star = Planet.MOON
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

class ApsisAzimuthFeature(private val apsisImpl: IApsis,
                          private val azimuthImpl: IAzimuthCalculator) : Feature<ApsisAzimuthConfig, Map<Apsis, StarPosWithAzimuth>> {
  override val key: String = "apsisAzimuth"

  override val defaultConfig: ApsisAzimuthConfig = ApsisAzimuthConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: ApsisAzimuthConfig): Map<Apsis, StarPosWithAzimuth> {
    val positionMap: Map<Apsis, IStarPos> = apsisImpl.getPositions(config.star, gmtJulDay, config.coordinate, config.nodeType)

    return positionMap.keys.associateWith { apsis ->
      val pos = positionMap.getValue(apsis)
      val azimuth: Azimuth = when (config.coordinate) {
        Coordinate.ECLIPTIC   -> azimuthImpl.getAzimuthFromEcliptic(pos, gmtJulDay, loc, config.temperature, config.pressure)
        Coordinate.EQUATORIAL -> azimuthImpl.getAzimuthFromEquator(pos, gmtJulDay, loc, config.temperature, config.pressure)
        else                  -> throw RuntimeException("No such coordinate : ${config.coordinate}")
      }
      StarPosWithAzimuth(pos, azimuth)
    }
  }
}
