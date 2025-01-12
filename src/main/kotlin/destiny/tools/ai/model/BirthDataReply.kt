/**
 * Created by smallufo on 2024-10-11.
 */
package destiny.tools.ai.model

import kotlinx.serialization.Serializable


@Serializable
data class BirthDataReply(
  val domains: Map<BirthDataDomain, String>,
  val followUps: List<FollowUp>
)
