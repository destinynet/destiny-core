/**
 * Created by smallufo on 2024-12-09.
 */
package destiny.core.ai.reply

import destiny.core.SynastryDomain
import destiny.tools.ai.model.FollowUp
import destiny.tools.ai.model.FormatSpec
import destiny.tools.ai.model.IFollowUps
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SynastryReply(
  val domains: Map<SynastryDomain, String>,
  val benefits: List<Statement>,
  val obstacles : List<Statement>,
  override val followUps: List<FollowUp>,
  val score: Score
  ): IFollowUps {

  @Serializable
  data class Statement(
    val point: String,
    val rationale: String,
  )

  @Serializable
  data class Score(
    @SerialName("outer_to_inner")
    val outerToInner : Int,
    @SerialName("inner_to_outer")
    val innerToOuter : Int
  )

  companion object {
    val formatSpec = FormatSpec.of<SynastryReply>("synastry", "Response format for synastry analysis")
  }
}
