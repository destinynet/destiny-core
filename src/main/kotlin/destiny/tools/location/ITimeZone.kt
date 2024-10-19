/** 2009/10/21 上午2:44:14 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.LatValue
import destiny.core.calendar.LngValue
import java.util.*

/**
 * 從經緯度求 TimeZone
 */
interface ITimeZone {

  /** 從經緯度查詢 timezone  */
  suspend fun getTimeZoneId(lat: LatValue, lng: LngValue): String?

  suspend fun getTimeZone(lat: LatValue, lng: LngValue): TimeZone? {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }
  }
}

