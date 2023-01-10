/**
 * Created by smallufo on 2021-11-16.
 */
package destiny.tools.converters

import destiny.core.IBirthDataNamePlace
import java.io.Serializable
import javax.inject.Named


@Named
class BdnpMapConverter(private val bdnpConverter : IContextMap<IBirthDataNamePlace>) : BdnpConverter, Serializable {

  override fun getMap(bdnp: IBirthDataNamePlace, additional: Map<String, String>): Map<String, String> {
    return additional.toMutableMap().apply {
      putAll(bdnpConverter.getMap(bdnp))
    }.toMap()
  }
}
