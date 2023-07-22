/**
 * Created by smallufo on 2023-07-22.
 */
package destiny.core.astrology

import java.io.Serializable


interface IHouseConfig : Serializable {
  var houseSystem: HouseSystem
  var coordinate: Coordinate

  val houseConfig: HouseConfig
    get() = HouseConfig(houseSystem, coordinate)
}
