/**
 * Created by smallufo on 2021-01-07.
 */
package destiny.core.fengshui

import destiny.core.ITimeLoc
import destiny.tools.location.MapType
import java.io.Serializable

/**
 * 描述某時刻、某地點、某種地圖的 model
 * 通常用此 model 展開各種 zoom 的地圖
 */
interface ICompassMapsModel : ITimeLoc, Serializable {

  val place: String?

  /** 正北 or 磁北 */
  val northType: NorthType

  /** 內定為道路圖 */
  val mapType: MapType
}

data class CompassMapsModel(
  val timeLoc: ITimeLoc,
  override val place: String?,
  override val northType: NorthType,
  override val mapType: MapType
) : ICompassMapsModel, ITimeLoc by timeLoc
