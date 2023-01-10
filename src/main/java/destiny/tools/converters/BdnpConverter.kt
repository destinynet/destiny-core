package destiny.tools.converters

import destiny.core.IBirthDataNamePlace

interface BdnpConverter {
  fun getMap(bdnp: IBirthDataNamePlace, additional: Map<String, String> = emptyMap()): Map<String, String>
}
