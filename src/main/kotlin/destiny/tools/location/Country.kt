/**
 * Created by smallufo on 2024-11-17.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng


interface ICountry {
  val name: String
  val code: String
  val pois: List<Country.Poi>
  val latLng: ILatLng?
}

data class Country(
  override val name: String,
  val levels: Int,
  override val latLng: ILatLng? = null,
  override val code: String,
  override val pois: List<Poi> = emptyList(),
) : ICountry {

  init {
    require(levels in 1..3) { "Levels must be between 1 and 3, found $levels for '$name'." }
  }

  data class Poi(
    override val name: String,
    val regex: Regex? = null,
    override val latLng: ILatLng? = null,
    override val code: String,
    override val pois: List<Poi> = emptyList()) : ICountry {
    init {
      if (pois.isEmpty()) {
        requireNotNull(latLng) {
          "Poi '$name' with no children must have a valid latLng."
        }
      }
    }
  }
}
