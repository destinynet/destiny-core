/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.tools.ai.model

import destiny.tools.ai.Provider


@Deprecated("")
data class LlmReply<D>(
  val digest: String,
  val reply : D,
  val provider: Provider,
  val llmModel : String
)
