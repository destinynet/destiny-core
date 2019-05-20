/** 2009/10/21 上午2:44:14 by smallufo  */
package destiny.tools.location

import java.util.*

/**
 * 從經緯度求 TimeZone
 */
interface ITimeZone {

  /** 從經緯度查詢 timezone  */
  fun getTimeZoneId(lat: Double, lng: Double): String?

  fun getTimeZone(lat: Double, lng: Double): TimeZone? {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }
  }
}

