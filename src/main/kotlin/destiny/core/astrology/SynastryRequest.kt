package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SynastryRequest(
  @Contextual
  val inner : IBirthDataNamePlace,
  @Contextual
  val outer : IBirthDataNamePlace,
  val mode : SynastryMode,
)
