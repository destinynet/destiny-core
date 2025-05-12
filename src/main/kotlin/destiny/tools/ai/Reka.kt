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
  data class ChatModel(
    val model: String,
    val messages: List<Message>,
    /**  Defaults to 0.4. */
    val temperature: Double? = null,
    /** Defaults to 1024. */
    @SerialName("top_k")
    val topK: Int? = null,
    /** Defaults to 0.95. */
    @SerialName("top_p")
    val topP: Double? = null,
    val tools: List<OpenAi.FunctionDeclaration.Function>? = null,
    @SerialName("use_search_engine")
    val useSearchEngine: Boolean? = false,
    val stream: Boolean = false,
  ) {

    @SerialName("tool_choice")
    val toolChoice: ToolChoice? = if (!tools.isNullOrEmpty()) {
      ToolChoice.auto
    } else {
      null
    }

    enum class ToolChoice {
      auto , tool , none
    }
  }

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
