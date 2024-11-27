/**
 * @author smallufo
 * Created on 2011/4/12 at 上午10:41:20
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import java.util.*

interface TimeZoneService {


  /** 嘗試從經緯度，尋找 TimeZone  */
  fun getTimeZoneId(lat: Lat, lng: Lng): String?

  fun getTimeZoneId(latLng: ILatLng): String? {
    return getTimeZoneId(latLng.lat, latLng.lng)
  }

  fun getTimeZone(lat: Lat, lng: Lng): TimeZone? {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }
  }

  fun getTimeZoneOrGMT(lat: Lat, lng: Lng): TimeZone {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) } ?: TimeZone.getTimeZone("GMT")
  }
}
