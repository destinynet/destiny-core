/**
 * Created by smallufo on 2024-10-11.
 */
package destiny.tools.ai.model

import kotlinx.serialization.Serializable


@Serializable
data class BirthDataReply(
  val domains: Map<BirthDataDomain, String>,
  override val followUps: List<FollowUp>
): IFollowUps {

  companion object {
    val formatSpec = FormatSpec.of<BirthDataReply>("birth_data_reply", "Response format for birth data analysis")
  }
}
