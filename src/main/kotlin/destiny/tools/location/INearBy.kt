/**
 * Created by smallufo on 2024-04-21.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.IPlace
import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import java.io.Serializable
import java.util.*

interface IPoi : ILatLng, IPlace, Serializable {
  val name: String
  val placeId: String
  val userRatingsTotal: Int?
}

data class GMapPoi(override val name: String, override val lat: Lat, override val lng: Lng, override val placeId: String, override val userRatingsTotal: Int?) : IPoi {
  override val place: String = name
}

interface INearByPoi : IPoi {
  val meters: Int
}

data class NearByPoi(val poi: IPoi, override val meters: Int) : INearByPoi, IPoi by poi

interface INearBy {

  suspend fun getNearBy(lat: Lat, lng: Lng, type: String, keyword: String?, radiusMeters: Int, locale: Locale = Locale.getDefault()): List<IPoi>

  suspend fun getNearBys(lat: Lat, lng: Lng, types: List<String>, radiusMeters: Int, locale: Locale = Locale.getDefault()): List<INearByPoi>
}
