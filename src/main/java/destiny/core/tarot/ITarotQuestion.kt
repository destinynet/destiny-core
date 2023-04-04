/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import destiny.core.Gender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ITarotQuestion {
  val spread: ISpread
  val gender: Gender?
  val question: String?
}

@Serializable
@SerialName("TarotQuestion")
data class TarotQuestion(
  override val spread: ISpread,
  override val gender: Gender?,
  override val question: String?
) : ITarotQuestion
