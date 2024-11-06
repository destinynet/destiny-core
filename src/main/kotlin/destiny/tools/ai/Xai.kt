/**
 * Created by smallufo on 2024-11-06.
 */
package destiny.tools.ai

import destiny.tools.ai.Cohere.ToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
  data class Response(val id: String, val `object`: String, val created: Long, val choices: List<Choice>) {

    @Serializable
    data class Choice(
      val index: Int,
      val message: Message,
      @SerialName("finish_reason")
      val finishReason: String
    )
  }

  @Serializable
  data class ToolFunction(val function: Function, val type: String = "function")

  @Serializable
  data class Function(val name: String, val description: String, val parameters: InputSchema)
}
