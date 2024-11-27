/** 2009/11/27 下午7:20:26 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import java.util.*

/**
 * 從經緯度尋找附近的地名
 */
interface ReverseGeocoder {

  suspend fun reverseGeocoding(lat: Lat, lng: Lng, locale: Locale = Locale.getDefault()): String?

}

