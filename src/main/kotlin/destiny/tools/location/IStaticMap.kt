/**
 * Created by smallufo on 2019-12-18.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import java.awt.Color
import java.util.*
import destiny.tools.defaultLocale

interface IStaticMap {

  data class Marker(val latLng: ILatLng,
                    val color: Color = Color.decode("#EA4335"),
                    val label: Char? = null)

  /**
   * return PNG byte array
   * @param center 緯度、經度
   * @param scale 1,2 or 4
   * @param styles 地圖樣式，每筆一個 Google Static Maps 的 `style` 值（例：`feature:poi.business|visibility:off`）；不支援的實作可忽略
   * */
  suspend fun getImage(center: ILatLng,
                       width: Int = 640, height: Int = 640,
                       zoom : Int = 15,
                       mapType: MapType = MapType.roadmap,
                       scale: Int = 1,
                       locale: Locale = defaultLocale,
                       markers : List<Marker> = emptyList(),
                       styles: List<String> = emptyList()): ByteArray
}
