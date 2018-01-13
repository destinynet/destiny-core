/**
 * @author smallufo
 * Created on 2011/4/12 at 上午11:26:31
 */
package destiny.tools.location

import java.util.*

interface ReverseGeocodingService {

  fun getNearbyLocation(lng: Double, lat: Double, locale: Locale): String?

  fun getNearbyLocationOpt(lng: Double, lat: Double, locale: Locale): Optional<String> {
    return getNearbyLocation(lng , lat , locale).let { Optional.ofNullable(it) }
  }
}
