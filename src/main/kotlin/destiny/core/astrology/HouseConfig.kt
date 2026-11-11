/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core.astrology

import kotlinx.serialization.Serializable


@Serializable
data class HouseConfig(
  override var houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  override var coordinate: Coordinate = Coordinate.ECLIPTIC
): IHouseConfig
