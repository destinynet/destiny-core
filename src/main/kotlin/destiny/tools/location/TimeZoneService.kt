/**
 * @author smallufo
 * Created on 2011/4/12 at 上午10:41:20
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.LatValue
import destiny.core.calendar.LngValue
import java.util.*

interface TimeZoneService {


  /** 嘗試從經緯度，尋找 TimeZone  */
  fun getTimeZoneId(lat: LatValue, lng: LngValue): String?

  fun getTimeZoneId(latLng: ILatLng): String? {
    return getTimeZoneId(latLng.lat, latLng.lng)
  }

  fun getTimeZone(lat: LatValue, lng: LngValue): TimeZone? {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }
  }

  fun getTimeZoneOrGMT(lat: LatValue, lng: LngValue): TimeZone {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) } ?: TimeZone.getTimeZone("GMT")
  }
}
