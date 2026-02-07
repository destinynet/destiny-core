/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.JsonSchemaSpec
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


class Groq {

  @Serializable(with = GroqMessageSerializer::class)
  sealed class Message {
    abstract val role: String

    data class TextContent(
      override val role: String,
      val content: String?,
      val reasoning: String? = null,
      val toolCallId: String? = null,
      val toolCalls: List<OpenAi.Message.ToolCall>? = null
    ) : Message()

    data class ChunkContent(
      override val role: String,
      val content: List<OpenAi.ContentChunk>
    ) : Message()
  }

  object GroqMessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GroqMessage") {
      element<String>("role")
      element<JsonElement>("content")
      element<String>("reasoning", isOptional = true)
      element<String>("tool_call_id", isOptional = true)
      element<JsonElement>("tool_calls", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Message) {
      require(encoder is JsonEncoder)
      val json = encoder.json
      val jsonElement = when (value) {
        is Message.TextContent  -> buildJsonObject {
          put("role", value.role)
          value.content?.let { put("content", it) }
          value.reasoning?.let { put("reasoning", it) }
          value.toolCallId?.let { put("tool_call_id", it) }
          value.toolCalls?.let { put("tool_calls", json.encodeToJsonElement(it)) }
        }
        is Message.ChunkContent -> buildJsonObject {
          put("role", value.role)
          put("content", json.encodeToJsonElement(value.content))
        }
      }
      encoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): Message {
      require(decoder is JsonDecoder)
      val json = decoder.json
      val jsonObject = decoder.decodeJsonElement().jsonObject
      val role = jsonObject["role"]!!.jsonPrimitive.content
      val content = jsonObject["content"]?.let {
        when {
          it is JsonNull      -> null
          it is JsonPrimitive -> it.content
          else                -> null
        }
      }
      val reasoning = jsonObject["reasoning"]?.jsonPrimitive?.contentOrNull
      val toolCallId = jsonObject["tool_call_id"]?.jsonPrimitive?.contentOrNull
      val toolCalls = jsonObject["tool_calls"]?.let {
        if (it is JsonNull) null else json.decodeFromJsonElement<List<OpenAi.Message.ToolCall>>(it)
      }
      return Message.TextContent(role, content, reasoning, toolCallId, toolCalls)
    }
  }

  @Serializable
  data class Response(val id: String, val model: String, val choices: List<Choice>, val usage: Usage) {
    @Serializable
    data class Choice(val index: Int,
                      val message: Message,
                      @SerialName("finish_reason") val finishReason: String)

    @Serializable
    data class Usage(
      @SerialName("prompt_tokens")
      val promptTokens: Int,
      @SerialName("completion_tokens")
      val completionTokens: Int,
    )
  }

  @Serializable
  sealed class ResponseFormat {
    @Serializable
    @SerialName("text")
    data object Text : ResponseFormat()

    @Serializable
    @SerialName("json_object")
    data object JsonObject : ResponseFormat()
  }

  data class GroqOptions(
    /** 0 to 2 , default 1 */
    val temperature: Double? = 1.0,
    /** 0 to 1 , default 1 */
    val topP: Double? = null,
    /** -2.0 .. 2.0 , default 0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toGroq() : GroqOptions {
        return GroqOptions(
          this.temperature?.value?.let { it * 2 },
          this.topP?.value,
          this.frequencyPenalty?.value?.let { it * 2 },
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<Message>,
    val user: String?,
    @Transient
    val options: GroqOptions? = null,
    val tools: List<OpenAi.FunctionDeclaration>? = null,
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,

    @Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null
  ) {

    val temperature: Double? = this.options?.temperature

    @SerialName("top_p")
    val topP: Double? = this.options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = this.options?.frequencyPenalty

    @SerialName("response_format")
    val responseFormat: ResponseFormat = if (jsonSchemaSpec != null && jsonSchemaSpec.schema["type"] != JsonPrimitive("string")) {
      ResponseFormat.JsonObject
    } else {
      ResponseFormat.Text
    }
  }


}
