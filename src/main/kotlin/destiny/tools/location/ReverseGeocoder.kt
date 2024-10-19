/** 2009/11/27 下午7:20:26 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.LatValue
import destiny.core.calendar.LngValue
import java.util.*

/**
 * 從經緯度尋找附近的地名
 */
interface ReverseGeocoder {

  suspend fun reverseGeocoding(lat: LatValue, lng: LngValue, locale: Locale = Locale.getDefault()): String?

}

