/** 2009/11/27 下午7:20:26 by smallufo  */
package destiny.tools.location

import java.util.*

/**
 * 從經緯度尋找附近的地名
 */
interface ReverseGeocoder {

  suspend fun reverseGeocoding(lat: Double, lng: Double, locale: Locale = Locale.getDefault()): String?

}

