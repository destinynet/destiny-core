/**
 * Created by smallufo on 2024-10-18.
 */
package destiny.tools.ai

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class FunCall(
  val name: String,
  val parameters: Map<String, @Contextual Any>
)

