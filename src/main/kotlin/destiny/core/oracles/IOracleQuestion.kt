/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles

import destiny.core.Gender
import destiny.tools.serializers.GenderSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IOracleQuestion {
  val clause : IClause
  val gender: Gender?
  val question: String?
}

@Serializable
@SerialName("OracleQuestion")
data class OracleQuestion(
  @Contextual
  override val clause: IClause,
  @Serializable(with = GenderSerializer::class)
  override val gender: Gender?,
  override val question: String?) : IOracleQuestion
