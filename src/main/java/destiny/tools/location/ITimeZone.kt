/** 2009/10/21 上午2:44:14 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.Location
import java.util.*

/**
 * 從經緯度求 TimeZone
 */
interface ITimeZone {

  /** 從經緯度查詢 timezone  */
  fun getTimeZoneId(lng: Double, lat: Double): String?

  fun getTimeZoneId(ew: Location.EastWest,
                     lngDeg: Int,
                     lngMin: Int,
                     lngSec: Double,
                     nw: Location.NorthSouth,
                     latDeg: Int,
                     latMin: Int,
                     latSec: Double): String? {
    val lng = Location.getLongitude(ew, lngDeg, lngMin, lngSec)
    val lat = Location.getLatitude(nw, latDeg, latMin, latSec)
    return getTimeZoneId(lng, lat)
  }

  fun getTimeZone(lng: Double , lat:Double): TimeZone? {
    return getTimeZoneId(lng , lat)?.let { TimeZone.getTimeZone(it) }
  }
}

