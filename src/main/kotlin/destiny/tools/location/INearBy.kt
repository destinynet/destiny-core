/**
 * Created by smallufo on 2024-04-21.
 */
package destiny.tools.location

import java.util.*

data class Poi(val name: String, val lat: Double, val lng: Double, val placeId: String, val userRatingsTotal: Int?)

interface INearBy {

  suspend fun getNearBy(lat: Double, lng: Double, type: String, radiusMeters: Int, locale: Locale = Locale.getDefault()) : List<Poi>
}
