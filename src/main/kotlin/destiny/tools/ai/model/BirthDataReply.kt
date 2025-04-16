/**
 * Created by smallufo on 2024-10-11.
 */
package destiny.tools.ai.model

import destiny.tools.ai.toJsonSchema
import kotlinx.serialization.Serializable


@Serializable
data class BirthDataReply(
  val domains: Map<BirthDataDomain, String>,
  override val followUps: List<FollowUp>
): IFollowUps {

  companion object {
    val schema = BirthDataReply::class.toJsonSchema("birth_data_reply", "Response format for birth data analysis")
  }
}
