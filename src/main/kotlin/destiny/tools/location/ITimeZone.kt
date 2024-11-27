/** 2009/10/21 上午2:44:14 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import java.util.*

/**
 * 從經緯度求 TimeZone
 */
interface ITimeZone {

  /** 從經緯度查詢 timezone  */
  suspend fun getTimeZoneId(lat: Lat, lng: Lng): String?

  suspend fun getTimeZone(lat: Lat, lng: Lng): TimeZone? {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }
  }
}

