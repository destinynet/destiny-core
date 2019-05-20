/** 2009/11/27 下午7:20:26 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.EastWest
import destiny.core.calendar.Location
import destiny.core.calendar.NorthSouth
import java.util.*

/**
 * 從經緯度尋找附近的地名
 */
interface ReverseGeocoder {

  fun getNearbyLocation(lat: Double, lng: Double, locale: Locale = Locale.getDefault()): String?

}

