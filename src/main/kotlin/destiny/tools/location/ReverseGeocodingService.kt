/**
 * @author smallufo
 * Created on 2011/4/12 at 上午11:26:31
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.LatValue
import destiny.core.calendar.LngValue
import java.util.*

interface ReverseGeocodingService {

  fun reverseGeocoding(lat: LatValue, lng: LngValue, locale: Locale = Locale.getDefault()): String?

  fun reverseGeocoding(latLng : ILatLng, locale: Locale = Locale.getDefault()) : String? {
    return reverseGeocoding(latLng.lat, latLng.lng, locale)
  }

}
