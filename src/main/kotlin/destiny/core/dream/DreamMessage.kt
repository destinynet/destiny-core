/**
 * Created by smallufo on 2024-01-28.
 */
package destiny.core.dream

import kotlinx.serialization.Serializable

@Serializable
data class DreamMessage(
  val provider: String,
  val providerKey: String
)
