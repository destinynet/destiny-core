/**
 * Created by smallufo on 2024-08-24.
 */
package destiny.tools.ai

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator


class Claude {


  @Serializable
  data class Response(val id : String , val type : String , val role : String , val model : String ,
                      val content : List<Content>,
                      @SerialName("stop_reason")
                      val stopReason : String ,
                      @SerialName("stop_sequence")
                      val stopSequence: String?, val usage : Usage) {

    @Serializable
    data class Usage(
      @SerialName("input_tokens")
      val inputTokens : Int ,
      @SerialName("output_tokens")
      val outputTokens : Int
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonClassDiscriminator("type")
    sealed class Content {
      abstract val type: String

      @Serializable
      @SerialName("text")
      data class Text(override val type: String = "text", val text: String) : Content()

      @Serializable
      @SerialName("tool_use")
      data class ToolUse(override val type: String = "tool_use", val id: String, val name: String, val input: Map<String, String>) : Content()

      @Serializable
      @SerialName("error")
      data class Error(override val type: String = "error", val message: String) : Content()
    }

  }


  @Serializable
  data class Function(val name: String, val description: String, @SerialName("input_schema") val inputSchema: InputSchema) {
    @Serializable
    data class InputSchema(val type: String = "object", val properties: Map<String, Property>, val required: List<String>) {
      @Serializable
      data class Property(val type: String, val description: String)
    }
  }
}
