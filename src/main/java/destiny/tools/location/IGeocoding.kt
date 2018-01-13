/** 2009/11/26 上午10:38:15 by smallufo  */
package destiny.tools.location

import destiny.core.calendar.Location

/** 從地名尋找經緯度  */
interface IGeocoding {

  /** 先傳回緯度，再傳回經度 */
  fun getLatLng(placeName: String): Pair<Double, Double>?

  fun getLocation(placeName: String, timeZoneService: TimeZoneService): Location? {
    return getLatLng(placeName)?.let { pair ->
      val lat = pair.first
      val lng = pair.second
      val tz = timeZoneService.getTimeZoneOrGMT(lng, lat)
      Location(lng , lat , tz)
    }
  }
}

