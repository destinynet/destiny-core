/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.IFunctionDeclaration
import destiny.tools.ai.JsonSchemaSpec
import destiny.tools.ai.llm.OpenAi.FunctionDeclaration
import destiny.tools.ai.model.ResponseFormat
import kotlinx.serialization.*
import kotlinx.serialization.json.*


/**
 * Mistral chat schema —— wire shape 與 OpenAI 一致（無 `reasoning`），直接複用
 * [OpenAi.Message] / [OpenAi.ContentChunk] / [OpenAi.Message.ToolCall]，不再保留
 * 自家 Message sealed class。
 */
class Mistral {

  data class MistralOptions(
    /** 0 .. 1 */
    val temperature: Double? = null,
    /** 0 .. 1 */
    val topP: Double? = null,
    /** -2 .. 2 , default 0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toMistral() : MistralOptions {
        return MistralOptions(
          this.temperature?.value,
          this.topP?.value,
          this.frequencyPenalty?.value,
        )
      }
    }
  }

  @Serializable
  data class Choice(
    val message: OpenAi.Message,
    val index: Int,
    val logprobs: Int? = null,
    @SerialName("finish_reason") val finishReason: String?
  )

  @Serializable
  data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int? = null,
    @SerialName("total_tokens") val totalTokens: Int
  )

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<OpenAi.Message>,
    @Transient
    val options: MistralOptions? = null,
    @SerialName("max_tokens")
    val maxTokens: Int = 4096,
    @Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null,
    val tools: List<OpenAi.FunctionDeclaration>? = null,
  ) {

    val temperature: Double? = options?.temperature

    @SerialName("top_p")
    val topP: Double? = options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = options?.frequencyPenalty

    @SerialName("response_format")
    @Serializable
    val responseFormat: ResponseFormat = if (jsonSchemaSpec != null && jsonSchemaSpec.schema["type"] != JsonPrimitive("string")) {
      ResponseFormat.JsonSchemaResponse(jsonSchemaSpec)
    } else {
      ResponseFormat.TextResponse
    }
  }

  @Serializable
  sealed class Response {

    @Serializable
    data class ErrorResponse(val message: String, val type: String, val code: String) : Response()

    @Serializable
    data class NormalResponse(val id: String, val created: Long, val model: String,
                              val choices: List<Choice>,
                              val usage: Usage) : Response()
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

fun IFunctionDeclaration.toMistral(): FunctionDeclaration {
  return this.toOpenAi()
}
