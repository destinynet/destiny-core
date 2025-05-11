/**
 * Created by smallufo on 2025-02-06.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Reka {

  @Serializable
  data class Message(
    val role: String,
    val content: String
  )

  @Serializable
  data class ChatModel(val model: String, val messages: List<Message>, val temperature: Double? = null, val stream: Boolean = false)

  @Serializable
  data class V1Response(
    val id: String,
    val model: String,
    val responses: List<Response>,
    val usage: Usage,
  ) {

    @Serializable
    data class Response(
      @SerialName("finish_reason")
      val finishReason: String,
      val message: Message
    )

    @Serializable
    data class Usage(
      @SerialName("input_tokens")
      val inputTokens: Int,
      @SerialName("output_tokens")
      val outputTokens: Int
    )
  }

}
