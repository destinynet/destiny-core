package destiny.tools.ai

import destiny.tools.ai.model.ResponseFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class OpenAi {

  @Serializable
  data class Message(val role: String,
                     val content: String?,
                     @SerialName("tool_call_id") val toolCallId: String? = null,
                     @SerialName("tool_calls") val toolCalls: List<ToolCall>? = null) {

    @Serializable
    data class ToolCall(val id: String, val type: String = "function", val function: ToolCallFunction) {
      @Serializable
      data class ToolCallFunction(val name: String, val arguments: String)
    }
  }

  @Serializable
  sealed class Response {

    @Serializable
    @SerialName("error")
    data class ErrorResponse(val error: Error) : Response() {
      @Serializable
      data class Error(val message: String, val type: String, val code: String?)
    }

    @Serializable
    data class NormalResponse(val id: String,
                              /** always 'chat.completion' */
                              val `object`: String,
                              val created: Long,
                              val model: String,
                              val choices: List<Choice>,
                              val usage: Usage): Response() {
      @Serializable
      data class Choice(val message: Message, val index: Int, val logprobs: Int? = null, @SerialName("finish_reason") val finishReason: String?)

      @Serializable
      data class Usage(
        @SerialName("prompt_tokens") val promptTokens: Int,
        @SerialName("completion_tokens") val completionTokens: Int? = null,
        @SerialName("total_tokens") val totalTokens: Int
      )
    }
  }

  object ResponseSerializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
      return when {
        element.jsonObject.containsKey("error") -> Response.ErrorResponse.serializer()
        else                                    -> Response.NormalResponse.serializer()
      }
    }
  }

  @Serializable
  data class FunctionDeclaration(val type: String, val function: Function) {
    @Serializable
    data class Function(val name: String, val description: String, val parameters: InputSchema)
  }

  @Serializable
  data class ChatModel(
    val messages: List<Message>,
    val user: String?,
    val model: String,
    /* 0 to 2 */
    val temperature: Double? = null,
    val tools: List<FunctionDeclaration>? = null,
    @kotlinx.serialization.Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null
  ) {
    @SerialName("response_format")
    @Serializable
    val responseFormat: ResponseFormat = if (jsonSchemaSpec == null) {
      ResponseFormat.TextResponse
    } else {
      ResponseFormat.JsonSchemaResponse(jsonSchemaSpec)
    }
  }
}
