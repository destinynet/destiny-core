/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles

import destiny.core.Gender
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
  override val clause: IClause,
  override val gender: Gender?,
  override val question: String?) : IOracleQuestion
