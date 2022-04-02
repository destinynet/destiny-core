/**
 * @author smallufo
 * Created on 2011/4/12 at 上午10:41:20
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import java.util.*

interface TimeZoneService {


  /** 嘗試從經緯度，尋找 TimeZone  */
  @Throws(Exception::class)
  fun getTimeZoneId(lat: Double, lng: Double): String?

  fun getTimeZoneId(latLng : ILatLng) : String ? {
    return getTimeZoneId(latLng.lat, latLng.lng)
  }

  fun getTimeZone(lat: Double, lng: Double): TimeZone? {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }
  }

  fun getTimeZoneOrGMT(lat: Double, lng: Double): TimeZone {
    return getTimeZoneId(lat, lng)?.let { TimeZone.getTimeZone(it) }?:TimeZone.getTimeZone("GMT")
  }
}
