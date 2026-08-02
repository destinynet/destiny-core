/**
 * Created by smallufo on 2023-07-22.
 */
package destiny.core.astrology

import destiny.tools.JSerializable


interface IHouseConfig : JSerializable {
  var houseSystem: HouseSystem
  var coordinate: Coordinate

  val houseConfig: HouseConfig
    get() = HouseConfig(houseSystem, coordinate)
}
