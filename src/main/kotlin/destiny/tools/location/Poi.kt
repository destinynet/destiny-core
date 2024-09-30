/**
 * Created by smallufo on 2019-07-27.
 */
package destiny.tools.location

import destiny.core.calendar.ILocationPlace
import destiny.core.calendar.Location
import destiny.core.calendar.LocationPlace
import destiny.tools.KotlinLogging
import destiny.tools.firstNotNullResult

data class Poi(val regex: Regex,
               val lat: Double?,
               val lng: Double?,
               val pois: List<Poi> = emptyList()) {

  private fun getLatLng(): Pair<Double, Double> {
    return if (lat != null && lng != null) {
      lat to lng
    } else {
      pois.first().getLatLng()
    }
  }

  fun locationPlace(string: String, prependAddress: String? = null, tzid: String? = null): ILocationPlace? {
    val thisResult: ILocationPlace? = regex.find(string)?.let { result ->
      val name = result.groupValues[0]
      val (lat, lng) = getLatLng()
      val loc = Location(lat, lng, tzid)
      LocationPlace(loc, prependAddress?.let { it + name } ?: name)
    }

    logger.debug("thisResult of {} = {}", regex, thisResult)

    val subResult: ILocationPlace? = thisResult?.let { r ->
      val thisName = r.place
      val stripName = string.replaceFirst(r.place, "")

      pois.asSequence().firstNotNullResult { subPoi -> subPoi.locationPlace(stripName, thisName, tzid) }
    }

    return subResult ?: thisResult
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}


