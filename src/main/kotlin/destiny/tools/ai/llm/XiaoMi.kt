/**
 * Created by smallufo on 2026-03-26.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.JsonSchemaSpec
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject


/**
 * XiaoMi (Mimo) chat schema —— wire shape 與 OpenAI 100% 相同（含 `reasoning_content`
 * 欄位、`tool_calls` 結構），故直接複用 [OpenAi.Message] / [OpenAi.Message.ToolCall]，
 * 不再保留自家 Message sealed class。
 */
class XiaoMi {

  @Serializable(with = ResponseSerializer::class)
  sealed class Response {

    @Serializable
    data class NormalResponse(val id: String, val model: String, val choices: List<Choice>, val usage: Usage) : Response() {
      @Serializable
      data class Choice(
        val index: Int,
        val message: OpenAi.Message,
        @SerialName("finish_reason") val finishReason: String
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
    data class ErrorResponse(val error: OpenAi.Response.ErrorResponse.Error) : Response()
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

  data class XiaoMiOptions(
    /** 0 to 1.5 , default varies by model */
    val temperature: Double? = null,
    /** 0.01 to 1.0 , default 0.95 */
    val topP: Double? = null,
    /** -2.0 .. 2.0 , default 0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toXiaoMi(): XiaoMiOptions {
        return XiaoMiOptions(
          this.temperature?.value?.let { it * 1.5 },
          this.topP?.value,
          this.frequencyPenalty?.value?.let { it * 2 },
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<OpenAi.Message>,
    val user: String?,
    @Transient
    val options: XiaoMiOptions? = null,
    val tools: List<OpenAi.FunctionDeclaration>? = null,
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
