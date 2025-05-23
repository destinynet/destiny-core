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
  data class MetaData(@SerialName("user_id") val userId: String)


  data class ClaudeOptions(
    val temperature: Double? = null,  // 0 < x < 1
    val topK: Int? = null,            // > 0
    val topP: Double? = null,         // 0 < x < 1
  ) {
    companion object {
      fun ChatOptions.toClaude() : ClaudeOptions {
        return ClaudeOptions(
          this.temperature?.value,
          this.topK?.value,
          this.topP?.value,
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val messages: List<ClaudeMessage>,
    // "claude-2.1" , "claude-3-opus-20240229" , "claude-3-5-sonnet-20240620"
    val model: String,

    @SerialName("max_tokens")
    val maxTokens: Int = 4096,

    @SerialName("metadata")
    val metadata: MetaData? = null,

    @Transient
    val options: ClaudeOptions? = null,

    val tools: List<Function>? = null
  ) {

    val temperature: Double? = options?.temperature

    @SerialName("top_k")
    val topK: Int? = options?.topK

    @SerialName("top_p")
    val topP: Double? = options?.topP
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
      val outputTokens : Int,
      @SerialName("cache_creation_input_tokens")
      val cacheCreationInputTokens : Int,
      @SerialName("cache_read_input_tokens")
      val cacheReadInputTokens : Int,
    )

    @Serializable
    data class Error(val type: String = "error", val message: String)

  }


  @Serializable
  data class Function(val name: String, val description: String, @SerialName("input_schema") val inputSchema: InputSchema)

}
