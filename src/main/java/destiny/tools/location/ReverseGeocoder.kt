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

  fun getNearbyLocation(lng: Double, lat: Double, locale: Locale): String?

  fun getNearbyLocation(locale: Locale,
                        ew: EastWest, lngDeg: Int, lngMin: Int, lngSec: Double,
                        nw: NorthSouth, latDeg: Int, latMin: Int, latSec: Double): String? {

    val lng = Location.getLongitude(ew, lngDeg, lngMin, lngSec)
    val lat = Location.getLatitude(nw, latDeg, latMin, latSec)
    return getNearbyLocation(lng, lat, locale)
  }
}

