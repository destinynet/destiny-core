/**
 * Created by smallufo on 2024-08-24.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.IFunctionDeclaration
import destiny.tools.ai.InputSchema
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


class Claude {

  /**
   * Anthropic prompt caching 控制 —— 放在 `text` content block 或 top-level `system` block 上,
   * 標記該 block 為可快取。
   *
   * - `type = "ephemeral"` 是目前 Anthropic 唯一支援的 cache type（5 分鐘 TTL 預設；
   *   可透過 `ttl = "1h"` + beta header 使用 1 小時 TTL）。
   * - `ttl` 可選 `"5m"`（預設）或 `"1h"`（beta，需 header `anthropic-beta:
   *   extended-cache-ttl-2025-04-11`）。
   *
   * cached block 在同一 user 後續 request 的 input tokens 計費打 1 折。
   *
   * API doc: https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching
   */
  @Serializable
  data class CacheControl(
    val type: String = "ephemeral",
    @SerialName("ttl") val ttl: String? = null,
  )

  /**
   * 頂層 `system` 欄位用的 text block（支援 cache_control）。
   * 與 messages[] 裡的 `Content.Text` 型別雷同，但 system 是獨立頂層欄位、不帶 role。
   */
  @Serializable
  data class SystemTextBlock(
    val type: String = "text",
    val text: String,
    @SerialName("cache_control")
    val cacheControl: CacheControl? = null,
  )

  @OptIn(ExperimentalSerializationApi::class)
  @Serializable
  @JsonClassDiscriminator("type")
  sealed class Content {
    abstract val contentType: String

    @Serializable
    @SerialName("text")
    data class Text(
      override val contentType: String = "text",
      val text: String,
      @SerialName("cache_control")
      val cacheControl: CacheControl? = null,
    ) : Content()

    @Serializable
    @SerialName("tool_use")
    data class ToolUse(override val contentType: String = "tool_use", val id: String, val name: String, val input: JsonElement) : Content()

    @Serializable
    @SerialName("tool_result")
    data class ToolResult(@SerialName("tool_use_id") val toolUseId: String, val content: String) : Content() {
      override val contentType: String = "tool_result"
    }

    @Serializable
    @SerialName("image")
    data class Image(
      override val contentType: String = "image",
      val source: ImageSource
    ) : Content() {
      @Serializable
      data class ImageSource(
        val type: String,
        @SerialName("media_type") val mediaType: String,
        val data: String
      )
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
    val maxTokens: Int = 8192,

    @SerialName("metadata")
    val metadata: MetaData? = null,

    @Transient
    val options: ClaudeOptions? = null,

    val tools: List<Function>? = null,

    /**
     * Top-level system prompt — Anthropic-native。
     * 通常放不變的背景（user profile / natal data / 指令），搭配 `cache_control = ephemeral`
     * 讓後續 turn 打 1 折。null 或空 list 就不送 `system` 欄位。
     */
    val system: List<SystemTextBlock>? = null,
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

fun IFunctionDeclaration.toClaude(): Claude.Function {
  return Claude.Function(
    this.name,
    this.description,
    InputSchema(
      "object",
      this.parameters.associate { p ->
        p.name to InputSchema.Property(
          p.type,
          p.description
        )
      },
      this.parameters.filter { it.required }.map { it.name }
    )
  )
}
