/** 2009/11/26 上午10:38:15 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import destiny.core.calendar.Location
import kotlinx.coroutines.runBlocking

/** 從地名尋找經緯度  */
interface IGeocoding {

  /** 先傳回緯度，再傳回經度 */
  suspend fun getLatLng(placeName: String): Pair<Lat, Lng>?

  fun getLocation(placeName: String, timeZoneService: TimeZoneService): Location? {
    return runBlocking {
      getLatLng(placeName)?.let { pair ->
        val lat = pair.first
        val lng = pair.second
        val tz = timeZoneService.getTimeZoneOrGMT(lat, lng)
        Location(lat, lng, tz.id)
      }
    }
  }
}

