/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai

import destiny.tools.ai.OpenAi.FunctionDeclaration
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


class Mistral {

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<OpenAi.Message>,
    val temperature: Double = 0.7,
    @SerialName("top_p")
    val topP: Double = 1.0,
    @SerialName("max_tokens")
    val maxTokens: Int = 4096,
    val tools: List<FunctionDeclaration>? = null
  )

  @Serializable
  sealed class Response {

    @Serializable
    data class ErrorResponse(val message: String, val type: String, val code: String) : Response()

    @Serializable
    data class NormalResponse(val id: String, val created: Long, val model: String, val choices: List<Choice>, val usage: Usage) : Response() {
      @Serializable
      data class Choice(
        val index: Int, val message: OpenAi.Message,
        @SerialName("finish_reason")
        val finishReason: String
      )

      @Serializable
      data class Usage(
        @SerialName("prompt_tokens")
        val promptTokens: Int,
        @SerialName("total_tokens")
        val totalTokens: Int,
        @SerialName("completion_tokens")
        val completionTokens: Int,
      )
    }
  }

  object ResponseSerializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
      return when (element.jsonObject["object"]!!.jsonPrimitive.content) {
        "error"           -> Response.ErrorResponse.serializer()
        "chat.completion" -> Response.NormalResponse.serializer()
        else              -> Response.NormalResponse.serializer()
      }
    }
  }


}
