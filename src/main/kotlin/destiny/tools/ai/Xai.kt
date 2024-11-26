/**
 * Created by smallufo on 2024-11-06.
 */
package destiny.tools.ai

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null
  )

  @Serializable
  data class Request(val messages: List<Message>,
                     val model: String,
                     val temperature: Double = 0.8,
                     val stream: Boolean = false,
                     val tools : List<ToolFunction> = emptyList()
  )


  @Serializable
  sealed class Response {

    @Serializable
    @SerialName("error")
    data class Error(val code: String , val error: String) : Response()

    @Serializable
    @SerialName("normal")
    data class NormalResponse(val id: String, val `object`: String, val created: Long, val choices: List<Choice>) : Response() {

      @Serializable
      data class Choice(
        val index: Int,
        val message: Message,
        @SerialName("finish_reason")
        val finishReason: String
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
