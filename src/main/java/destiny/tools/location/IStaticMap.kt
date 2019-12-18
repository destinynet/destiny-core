/**
 * Created by smallufo on 2019-12-18.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import java.awt.Color
import java.util.*

interface IStaticMap {

  enum class MapType {
    roadmap, satellite, hybrid, terrain
  }

  data class Marker(val latLng: ILatLng,
                    val color: Color = Color.decode("#EA4335"),
                    val label: Char? = null)

  /**
   * return PNG byte array
   * @param center 緯度、經度
   * */
  suspend fun getImage(center: ILatLng,
                       width: Int = 640, height: Int = 640,
                       zoom : Int = 15,
                       mapType: MapType = MapType.roadmap,
                       locale: Locale = Locale.getDefault() ,
                       markers : List<Marker> = emptyList()): ByteArray
}
