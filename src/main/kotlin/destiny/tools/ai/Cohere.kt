/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Cohere {

  @Serializable
  data class ToolCall(val id: String,
                      val type: String = "function",
                      val function: Function) {
    @Serializable
    data class Function(val name: String, val arguments: String)
  }

  @Serializable
  data class Content(val type: String = "text", val text: String)

  @Serializable
  data class Message(
    val role: String,
    val content: List<Content>?,
    @SerialName("tool_call_id")
    val toolCallId: String? = null,
    @SerialName("tool_plan")
    val toolPlan: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null
  )

  @Serializable
  data class Request(val model: String, val messages: List<Message>, val tools: List<ToolFunction>, val stream: Boolean = false)

  @Serializable
  data class Response(
    val id: String,
    @SerialName("finish_reason")
    val finishReason: String,
    val message: Message,
  )

  @Serializable
  data class ToolFunction(val type: String = "function", val function: Function) {

    @Serializable
    data class Function(val name: String, val description: String, val parameters: InputSchema)
  }
}