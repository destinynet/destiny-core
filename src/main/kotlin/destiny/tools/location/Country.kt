/**
 * Created by smallufo on 2024-11-16.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.LatValue
import destiny.core.calendar.LngValue


data class Country(val name: String, val code: String, val sections: List<Section>)
data class Section(val name: String, val code: String, val cities : List<City>)
data class City(val name: String, override val lat: LatValue, override val lng: LngValue, val unlocode: String, val regex: Regex? = null) : ILatLng
