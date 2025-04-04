/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.tools.ai

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


class Deepseek {

  @Serializable
  sealed class Response {

    @Serializable
    @SerialName("error")
    data class ErrorResponse(val error: Error) : Response() {
      @Serializable
      data class Error(val message: String, val type: String, val code: String)
    }

    @Serializable
    @SerialName("normal")
    data class NormalResponse(
      val id: String,
      /** always 'chat.completion' */
      val `object`: String,
      val created: Long,
      val model: String,
      val choices: List<Choice>,
    ): Response() {

      @Serializable
      data class Choice(val message: OpenAi.Message, val index: Int, val logprobs: Int? = null, @SerialName("finish_reason") val finishReason: String?)

      @Serializable
      data class Usage(
        @SerialName("prompt_tokens") val promptTokens: Int,
        @SerialName("completion_tokens") val completionTokens: Int? = null,
        @SerialName("total_tokens") val totalTokens: Int,
        @SerialName("prompt_cache_hit_tokens") val promptCacheHitTokens: Int,
        @SerialName("prompt_cache_miss_tokens") val promptCacheMissTokens: Int,
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
  sealed class ResponseFormat {
    @Serializable
    @SerialName("text")
    data object Text : ResponseFormat()
    @Serializable
    @SerialName("json_object")
    data object JsonObject : ResponseFormat()
  }

  @Serializable
  data class ChatModel(val messages: List<OpenAi.Message>,
                       val model: String,
                       val stream: Boolean? = false,
                       /** 0 <= value <= 2.0 */
                       val temperature: Double? = 1.0,
                       @SerialName("top_p")
                       val topP : Double? = 1.0,
                       val tools: List<OpenAi.FunctionDeclaration>? = null,
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
