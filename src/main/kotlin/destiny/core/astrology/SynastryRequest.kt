package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

interface ISynastryRequest {
  val inner: IBirthDataNamePlace
  val outer: IBirthDataNamePlace
  val mode: SynastryMode
}

@Serializable
data class SynastryRequest(
  @Contextual
  override val inner : IBirthDataNamePlace,
  @Contextual
  override val outer : IBirthDataNamePlace,
  override val mode : SynastryMode,
) : ISynastryRequest
