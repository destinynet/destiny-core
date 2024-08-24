/**
 * Created by smallufo on 2024-08-24.
 */
package destiny.tools.ai

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


class Claude {

  @Serializable(with = ClaudeMessageSerializer::class)
  sealed class ClaudeMessage {
    abstract val role: String

    abstract val stringContent: String

    @Serializable
    data class Text(override val role: String, val content: String) : ClaudeMessage() {

      @kotlinx.serialization.Transient
      override val stringContent = content
    }


    @Serializable
    data class ToolResult(
      override val role: String,
      val content: List<ToolResultContent>
    ) : ClaudeMessage() {
      @Serializable
      data class ToolResultContent(@SerialName("tool_use_id") val toolUseId: String, val content: String) {
        val type: String = "tool_result"
      }

      @kotlinx.serialization.Transient
      override val stringContent: String = content.joinToString("\n") { it.content }
    }
  }

  object ClaudeMessageSerializer : JsonContentPolymorphicSerializer<ClaudeMessage>(ClaudeMessage::class) {
    override fun selectDeserializer(element: JsonElement) = when {
      element.jsonObject["tool_use_id"] != null -> ClaudeMessage.ToolResult.serializer()
      else                                      -> ClaudeMessage.Text.serializer()
    }
  }


  @Serializable
  data class Response(val id : String?, val type : String, val role : String?, val model : String?,
                      @SerialName("content")
                      val contents : List<Content>?,
                      val error : Error?,
                      @SerialName("stop_reason")
                      val stopReason : String?,
                      @SerialName("stop_sequence")
                      val stopSequence: String?, val usage : Usage?) {

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

    }

    @Serializable
    data class Error(val type: String = "error", val message: String)

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
