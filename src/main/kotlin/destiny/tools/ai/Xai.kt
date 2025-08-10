/**
 * Created by smallufo on 2024-11-06.
 */
package destiny.tools.ai

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


class Xai {

  @Serializable
  data class ToolCall(val id: String,
                      val type: String = "function",
                      val function: Function) {
    @Serializable
    data class Function(val name: String, val arguments: String)
  }

  @Serializable
  data class Message(
    val role: String,
    val content: String,
    @SerialName("reasoning_content")
    val reasoning: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null
  )

  data class XaiOptions(
    /** 0 to 2 , default 1 */
    val temperature: Double? = null,
    val topP: Double? = null,
    /** -2 to 2 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toXai() : XaiOptions {
        return XaiOptions(
          this.temperature?.value?.let { it * 2 },
          this.topP?.value,
          this.frequencyPenalty?.value?.let { it * 2 },
        )
      }
    }
  }

  @Serializable
  data class Request(val messages: List<Message>,
                     val model: String,
                     @Transient
                     val options: XaiOptions? = null,
                     val stream: Boolean = false,
                     val tools : List<ToolFunction> = emptyList()
  ) {
    val temperature: Double? = options?.temperature

    @SerialName("top_p")
    val topP: Double? = options?.topP
  }


  @Serializable
  sealed class Response {

    @Serializable
    @SerialName("error")
    data class Error(val code: String , val error: String) : Response()

    @Serializable
    @SerialName("normal")
    data class NormalResponse(val id: String, val `object`: String, val created: Long, val model: String, val choices: List<Choice>, val usage: Usage) : Response() {

      @Serializable
      data class Choice(
        val index: Int,
        val message: Message,
        @SerialName("finish_reason")
        val finishReason: String
      )

      /**
       * example:
       * {
       *    "prompt_tokens":19,
       *    "completion_tokens":23,
       *    "total_tokens":245,
       *    "prompt_tokens_details":{
       *       "text_tokens":19,
       *       "audio_tokens":0,
       *       "image_tokens":0,
       *       "cached_tokens":3
       *    },
       *    "completion_tokens_details":{
       *       "reasoning_tokens":203,
       *       "audio_tokens":0,
       *       "accepted_prediction_tokens":0,
       *       "rejected_prediction_tokens":0
       *    },
       *    "num_sources_used":0
       * }
       */
      @Serializable
      data class Usage(
        @SerialName("prompt_tokens")
        val promptTokens : Int,
        @SerialName("completion_tokens")
        val completionTokens: Int,
        @SerialName("total_tokens")
        val totalTokens: Int
      )
    }
  }

  object ResponseSerializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
      return when {
        element.jsonObject.containsKey("error") -> Response.Error.serializer()
        else                                    -> Response.NormalResponse.serializer()
      }
    }
  }



  @Serializable
  data class ToolFunction(val function: Function, val type: String = "function")

  @Serializable
  data class Function(val name: String, val description: String, val parameters: InputSchema)
}
