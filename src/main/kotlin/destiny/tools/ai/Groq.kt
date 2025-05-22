/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai

import destiny.tools.ai.OpenAi.FunctionDeclaration
import destiny.tools.ai.OpenAi.Message.ToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonPrimitive


class Groq {

  @Serializable
  data class Message(val role: String,
                     val content: String?,
                     @Transient
                     val reasoning: String? = null,
                     @SerialName("tool_call_id") val toolCallId: String? = null,
                     @SerialName("tool_calls") val toolCalls: List<ToolCall>? = null
  )

  @Serializable
  data class Response(val id: String, val model: String, val choices: List<Choice>, val usage: Usage) {
    @Serializable
    data class Choice(val index: Int,
                      val message: Message,
                      @SerialName("finish_reason") val finishReason: String)

    @Serializable
    data class Usage(
      @SerialName("prompt_tokens")
      val promptTokens: Int,
      @SerialName("completion_tokens")
      val completionTokens: Int,
    )
  }

  @Serializable
  sealed class ResponseFormat {
    @Serializable
    @SerialName("text")
    data object Text : ResponseFormat()

    @Serializable
    @SerialName("json_object")
    data object JsonObject : ResponseFormat()
  }

  data class GroqOptions(
    /** 0 to 2 , default 1 */
    val temperature: Double? = 1.0,
    /** 0 to 1 , default 1 */
    val topP: Double? = null,
    /** -2.0 .. 2.0 , default 0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toGroq() : GroqOptions {
        return GroqOptions(
          this.temperature?.value?.let { it * 2 },
          this.topP?.value,
          this.frequencyPenalty?.value?.let { it * 2 },
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<Message>,
    val user: String?,
    @Transient
    val options: GroqOptions? = null,
    val tools: List<FunctionDeclaration>? = null,
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,

    @Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null
  ) {

    val temperature: Double? = this.options?.temperature

    @SerialName("top_p")
    val topP: Double? = this.options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = this.options?.frequencyPenalty

    @SerialName("response_format")
    val responseFormat: ResponseFormat = if (jsonSchemaSpec != null && jsonSchemaSpec.schema["type"] != JsonPrimitive("string")) {
      ResponseFormat.JsonObject
    } else {
      ResponseFormat.Text
    }
  }


}
