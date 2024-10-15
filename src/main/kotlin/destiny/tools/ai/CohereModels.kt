/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Cohere {

  @Serializable
  data class ReqMessage(val role: String, val content: String)

  @Serializable
  data class Request(val model: String, val messages: List<ReqMessage>, val tools: List<ToolFunction>, val stream: Boolean = false)

  @Serializable
  data class Content(val type: String, val text: String)

  @Serializable
  data class ResMessage(
    val role: String,
    val content: List<Content>?,
    @SerialName("tool_plan")
    val toolPlan: String?,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>?
  ) {

    @Serializable
    data class ToolCall(
      val id: String,
      val type: String = "function",
      val function: Function
    ) {
      @Serializable
      data class Function(val name: String, val arguments: String)
    }
  }

  @Serializable
  data class Response(
    val id: String,
    @SerialName("finish_reason")
    val finishReason: String,
    val message: ResMessage,
  )

  @Serializable
  data class ToolFunction(val type: String = "function", val function: Function) {

    @Serializable
    data class Function(val name: String, val description: String, val parameters: InputSchema)
  }
}
