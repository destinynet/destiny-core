/**
 * Created by smallufo on 2025-05-12.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


class Together {

  data class TogetherOptions(
    /** A decimal number from 0-1 */
    val temperature: Double? = null,
    val topK: Int? = null,
    val topP: Double? = null,
    /** -2 .. 2 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toTogether() : TogetherOptions {
        return TogetherOptions(
          this.temperature?.value,
          this.topK?.value,
          this.topP?.value,
          this.frequencyPenalty?.value?.let { it * 2 }
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<OpenAi.Message>,
    @Transient
    val options: TogetherOptions? = null,
    val stream: Boolean = false
  ) {
    val temperature: Double? = options?.temperature

    @SerialName("top_k")
    val topK: Int? = options?.topK

    @SerialName("top_p")
    val topP: Double? = options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = options?.frequencyPenalty

  }
}
