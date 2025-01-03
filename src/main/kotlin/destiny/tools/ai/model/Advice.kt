/**
 * Created by smallufo on 2024-12-25.
 */
package destiny.tools.ai.model

import destiny.tools.ai.Provider
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Advice(
  val summary: String,
  val pros: List<String> = emptyList(),
  val cons: List<String> = emptyList(),
  val actions: List<String> = emptyList(),
  var provider: Provider? = null,
  var model: String? = null
) : IDigestResponse, Serializable
