/** 2009/11/26 上午10:38:15 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.Location
import java.util.*

/** 從地名尋找經緯度  */
interface IGeocoding {

  fun getLongLat(placeName: String): Pair<Double, Double>?

  fun getLocation(placeName: String, timeZoneService: TimeZoneService): Optional<Location> {

    return getLongLat(placeName)?.let { pair ->
      val lng = pair.first
      val lat = pair.second
      val tz = timeZoneService.getTimeZone(lng, lat)
      Location(lng, lat, tz)
    }.let { Optional.ofNullable(it) }

  }
}

