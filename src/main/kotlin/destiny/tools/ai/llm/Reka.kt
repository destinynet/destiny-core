/**
 * Created by smallufo on 2025-02-06.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


class Reka {

  @Serializable
  data class Message(
    val role: String,
    val content: String
  )

  data class RekaOptions(
    /** Defaults to 0.4. */
    val temperature: Double? = null,
    /** Defaults to 1024. */
    val topK: Int? = null,
    /** Defaults to 0.95. */
    val topP: Double? = null,
    /** defaults to 0.0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toReka() : RekaOptions {
        return RekaOptions(
          this.temperature?.value,
          this.topK?.value,
          this.topP?.value,
          this.frequencyPenalty?.value
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<Message>,
    @Transient
    val options: RekaOptions? = null,
    val tools: List<OpenAi.FunctionDeclaration.Function>? = null,
    @SerialName("use_search_engine")
    val useSearchEngine: Boolean? = false,
    val stream: Boolean = false,
  ) {

    val temperature: Double? = options?.temperature

    @SerialName("top_k")
    val topK: Int? = options?.topK

    @SerialName("top_p")
    val topP: Double? = options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = options?.frequencyPenalty

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
      val outputTokens: Int,
      @SerialName("reasoning_tokens")
      val reasoningTokens: Int? = null
    )
  }

}
