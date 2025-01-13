/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.tools.ai.model

import kotlinx.serialization.Serializable


@Serializable
data class FengShuiAerialReply(
  val domains : Map<FengShuiDomain, String>
)
