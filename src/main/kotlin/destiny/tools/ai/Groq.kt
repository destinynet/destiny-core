/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai

import destiny.tools.ai.OpenAi.FunctionDeclaration
import destiny.tools.ai.OpenAi.Message.ToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


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
    data class Choice(
      val index: Int, val message: Message, @SerialName("finish_reason") val finishReason: String
    )

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

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<Message>,
    val user: String?,
    /**
     * 0 to 2 , default 1
     */
    val temperature: Double = 1.0,
    /**
     * 0 to 1 , default 1
     */
    @SerialName("top_p")
    val topP: Double? = null,
    val tools: List<FunctionDeclaration>? = null,
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,

    @kotlinx.serialization.Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null
  ) {

    @SerialName("response_format")
    val responseFormat: ResponseFormat = if (jsonSchemaSpec == null) {
      ResponseFormat.Text
    } else {
      ResponseFormat.JsonObject
    }
  }


}
