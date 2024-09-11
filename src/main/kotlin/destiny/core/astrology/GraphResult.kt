package destiny.core.astrology

import destiny.core.Circular

data class GraphResult(
  val circles: Set<Circular<Planet>>,
  val paths: Set<List<Planet>>,
  val isolated: Set<Planet>,
  val terminals: Set<Planet>
)
