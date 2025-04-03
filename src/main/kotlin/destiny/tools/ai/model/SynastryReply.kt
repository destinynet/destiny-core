/**
 * Created by smallufo on 2024-12-09.
 */
package destiny.tools.ai.model

import destiny.core.SynastryDomain
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
}
