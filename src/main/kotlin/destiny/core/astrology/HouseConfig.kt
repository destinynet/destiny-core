/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable



@Serializable
data class HouseConfig(
  override var houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override var coordinate: Coordinate = Coordinate.ECLIPTIC
): IHouseConfig

@Deprecated("config DSL 將移除：改用 HouseConfig(...) 具名參數或 copy()。")
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
