/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


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

  data class CohereOptions(
    val temperature: Double? = 0.3,
    /** max value of 500 */
    val k: Int? = null,
    /** Defaults to 0.75. min value of 0.01, max value of 0.99. */
    val p: Double? = 0.75,
    /** 0 t0 1 , default 0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toCohere() : CohereOptions {
        return CohereOptions(
          this.temperature?.value,
          this.topK?.value,
          this.topP?.value,
          this.frequencyPenalty?.value?.let { (it + 1) / 2.0 }
        )
      }
    }
  }

  @Serializable
  data class Request(val model: String,
                     val messages: List<Message>,
                     val tools: List<ToolFunction>,
                     val stream: Boolean = false,
                     @Transient
                     val options: CohereOptions? = null,
                     ) {
    val temperature: Double? = options?.temperature
    val k: Int? = options?.k
    val p: Double? = options?.p
    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = options?.frequencyPenalty
  }

  @Serializable
  data class Response(
    val id: String,
    @SerialName("finish_reason")
    val finishReason: String,
    val message: Message,
    val usage: Usage,
  ) {
    @Serializable
    data class InputOutputTokens(
      @SerialName("input_tokens")
      val input : Int ,
      @SerialName("output_tokens")
      val output: Int
    )

    @Serializable
    data class Usage(
      @SerialName("billed_units")
      val billedUnits : InputOutputTokens,
      @SerialName("tokens")
      val tokens: InputOutputTokens,
    )
  }

  @Serializable
  data class ToolFunction(val type: String = "function", val function: Function) {

    @Serializable
    data class Function(val name: String, val description: String, val parameters: InputSchema)
  }
}
