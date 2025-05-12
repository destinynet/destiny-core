/**
 * Created by smallufo on 2025-05-12.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Together {

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<OpenAi.Message>,
    /** A decimal number from 0-1 */
    val temperature: Double? = null,
    @SerialName("top_k")
    val topK: Int? = null,
    @SerialName("top_p")
    val topP: Double? = null,
    val stream: Boolean = false
  )
}
