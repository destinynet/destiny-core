/**
 * Created by smallufo on 2024-08-24.
 */
package destiny.tools.ai

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


class Claude {

  @OptIn(ExperimentalSerializationApi::class)
  @Serializable
  @JsonClassDiscriminator("type")
  sealed class Content {
    abstract val contentType: String

    @Serializable
    @SerialName("text")
    data class Text(override val contentType: String = "text", val text: String) : Content()

    @Serializable
    @SerialName("tool_use")
    data class ToolUse(override val contentType: String = "tool_use", val id: String, val name: String, val input: JsonElement) : Content()

    @Serializable
    @SerialName("tool_result")
    data class ToolResult(@SerialName("tool_use_id") val toolUseId: String, val content: String) : Content() {
      override val contentType: String = "tool_result"
    }
  }

  @Serializable(with = ClaudeMessageSerializer::class)
  sealed class ClaudeMessage {
    abstract val role: String

    @Serializable
    data class TextContent(override val role: String, val content: String) : ClaudeMessage()

    @Serializable
    data class ArrayContent(override val role: String, val content: List<Content>) : ClaudeMessage()
  }


  object ClaudeMessageSerializer : KSerializer<ClaudeMessage> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ClaudeMessage") {
      element<String>("role")
      element<JsonElement>("content")
    }

    override fun serialize(encoder: Encoder, value: ClaudeMessage) {
      val compositeOutput = encoder.beginStructure(descriptor)
      compositeOutput.encodeStringElement(descriptor, 0, value.role)

      when (value) {
        is ClaudeMessage.TextContent  -> compositeOutput.encodeSerializableElement(descriptor, 1, serializer(), JsonPrimitive(value.content))
        is ClaudeMessage.ArrayContent -> compositeOutput.encodeSerializableElement(descriptor, 1, serializer(), Json.encodeToJsonElement(value.content))
      }

      compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): ClaudeMessage {
      throw UnsupportedOperationException("Deserialization is not supported")
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

    @Serializable
    data class Error(val type: String = "error", val message: String)

  }


  @Serializable
  data class Function(val name: String, val description: String, @SerialName("input_schema") val inputSchema: InputSchema)

}
