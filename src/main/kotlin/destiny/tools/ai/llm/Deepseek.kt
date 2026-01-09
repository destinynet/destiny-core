/**
 * Created by smallufo on 2024-12-26.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.IFunctionDeclaration
import destiny.tools.ai.JsonSchemaSpec
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
      val usage: Usage,
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

  data class DeepseekOptions(
    /** 0 <= value <= 2.0 */
    val temperature: Double? = 1.0,
    val topP: Double? = 1.0,
    /** -2 .. 2 */
    val frequencyPenalty: Double? = 0.0,
  ) {
    companion object {
      fun ChatOptions.toDeepseek() : DeepseekOptions{
        return DeepseekOptions(
          this.temperature?.value?.let { it * 2 },
          this.topP?.value,
          this.frequencyPenalty?.value,
        )
      }
    }
  }

  @Serializable
  data class ChatModel(val messages: List<OpenAi.Message>,
                       val model: String,
                       val stream: Boolean? = false,
                       @Transient
                       val options : DeepseekOptions? = null,
                       val tools: List<OpenAi.FunctionDeclaration>? = null,
                       @Transient
                       val jsonSchemaSpec: JsonSchemaSpec? = null
  ) {

    val temperature: Double? = options?.temperature

    @SerialName("top_p")
    val topP: Double? = options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = options?.frequencyPenalty

    @SerialName("response_format")
    val responseFormat: ResponseFormat = if (jsonSchemaSpec == null) {
      ResponseFormat.Text
    } else {
      ResponseFormat.JsonObject
    }
  }
}

fun IFunctionDeclaration.toDeepseek(): OpenAi.FunctionDeclaration {
  return this.toOpenAi()
}
