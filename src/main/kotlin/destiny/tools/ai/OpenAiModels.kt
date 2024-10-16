package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class OpenAi {

  @Serializable
  data class OpenAiMsg(val role: String,
                       val content: String?,
                       @SerialName("tool_call_id")
                       val toolCallId: String? = null,
                       @SerialName("tool_calls")
                       val toolCalls: List<ToolCall>? = null) {

    @Serializable
    data class ToolCall(val id: String, val type: String, val function: ToolCallFunction) {
      @Serializable
      data class ToolCallFunction(val name: String, val arguments: String)
    }
  }


  @Serializable
  data class Result(val id: String,
                    /** always 'chat.completion' */
                    val `object`: String,
                    val created: Long,
                    /** maybe 'gpt-3.5-turbo-0301' */
                    val model: String,
                    val choices: List<Choice>,
                    val usage: Usage) {
    @Serializable
    data class Choice(val message: OpenAiMsg, val index: Int, val logprobs: Int? = null, @SerialName("finish_reason") val finishReason: String?)

    @Serializable
    data class Usage(
      @SerialName("prompt_tokens") val promptTokens: Int,
      @SerialName("completion_tokens") val completionTokens: Int? = null,
      @SerialName("total_tokens") val totalTokens: Int
    )

  }

  @Serializable
  data class Error(val message: String, val type: String, val code: String? = null)

  @Serializable
  data class FunctionDeclaration(val type: String, val function: Function) {
    @Serializable
    data class Function(val name: String, val description: String, val parameters: Parameters) {
      @Serializable
      data class Parameters(val type: String, val properties: Map<String, Argument>, val required: List<String>) {
        @Serializable
        data class Argument(val type: String, val enum: List<String>? = null, val description: String)
      }
    }
  }

  @Serializable
  data class ChatModel(val messages: List<OpenAiMsg>, val user: String?, val model: String, val tools: List<FunctionDeclaration>? = null)
}



