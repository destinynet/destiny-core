/**
 * @author smallufo
 * Created on 2011/4/12 at 上午11:26:31
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import java.util.*

interface ReverseGeocodingService {

  fun getNearbyLocation(lat: Double, lng: Double, locale: Locale = Locale.getDefault()): String?

  fun getNearbyLocation(latLng : ILatLng, locale: Locale = Locale.getDefault()) : String? {
    return getNearbyLocation(latLng.lat , latLng.lng, locale)
  }

}
