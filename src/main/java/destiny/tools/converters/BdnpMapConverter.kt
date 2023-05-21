/**
 * Created by smallufo on 2021-11-16.
 */
package destiny.tools.converters

import destiny.core.IBirthDataNamePlace
import jakarta.inject.Named
import java.io.Serializable


// encrypted version
@Named
class BdnpMapConverter(private val birthDataNamePlaceConverter : IContextMap<IBirthDataNamePlace>) : BdnpConverter, Serializable {

  override fun getMap(bdnp: IBirthDataNamePlace, additional: Map<String, String>): Map<String, String> {
    return additional.toMutableMap().apply {
      putAll(birthDataNamePlaceConverter.getMap(bdnp))
    }.toMap()
  }
}
