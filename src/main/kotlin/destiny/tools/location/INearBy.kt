/**
 * Created by smallufo on 2024-04-21.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.IPlace
import java.util.*

interface IPoi : ILatLng, IPlace {
  val name: String
  val placeId: String
  val userRatingsTotal: Int?
}

data class Poi(override val name: String, override val lat: Double, override val lng: Double, override val placeId: String, override val userRatingsTotal: Int?) : IPoi {
  override val place: String = name
}

interface INearByPoi : IPoi {
  val meters: Int
}

data class NearByPoi(val poi: IPoi, override val meters: Int) : INearByPoi, IPoi by poi

interface INearBy {

  suspend fun getNearBy(lat: Double, lng: Double, type: String, radiusMeters: Int, locale: Locale = Locale.getDefault()): List<IPoi>

  suspend fun getNearBys(lat: Double, lng: Double, types: List<String>, radiusMeters: Int, locale: Locale = Locale.getDefault()): List<INearByPoi>
}
