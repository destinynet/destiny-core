/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable

@Serializable
data class HouseConfig(
  val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  val coordinate: Coordinate = Coordinate.ECLIPTIC
): java.io.Serializable

@DestinyMarker
class HouseConfigBuilder : Builder<HouseConfig> {
  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
  var coordinate: Coordinate = Coordinate.ECLIPTIC

  override fun build(): HouseConfig {
    return HouseConfig(houseSystem, coordinate)
  }

  companion object {
    fun houseCusp(block: HouseConfigBuilder.() -> Unit = {}): HouseConfig {
      return HouseConfigBuilder().apply(block).build()
    }
  }
}
