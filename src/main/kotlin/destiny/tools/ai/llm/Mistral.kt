/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.IFunctionDeclaration
import destiny.tools.ai.JsonSchemaSpec
import destiny.tools.ai.llm.OpenAi.FunctionDeclaration
import destiny.tools.ai.model.ResponseFormat
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


class Mistral {

  data class MistralOptions(
    /** 0 .. 1 */
    val temperature: Double? = null,
    /** 0 .. 1 */
    val topP: Double? = null,
    /** -2 .. 2 , default 0 */
    val frequencyPenalty: Double? = null,
  ) {
    companion object {
      fun ChatOptions.toMistral() : MistralOptions {
        return MistralOptions(
          this.temperature?.value,
          this.topP?.value,
          this.frequencyPenalty?.value,
        )
      }
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  @Serializable
  @JsonClassDiscriminator("type")
  sealed class ContentChunk {
    @Serializable
    @SerialName("text")
    data class TextChunk(val text: String) : ContentChunk()

    @Serializable
    @SerialName("image_url")
    data class ImageURLChunk(@SerialName("image_url") val imageUrl: ImageUrl) : ContentChunk() {
      @Serializable
      data class ImageUrl(val url: String)
    }
  }

  @Serializable(with = MistralMessageSerializer::class)
  sealed class Message {
    abstract val role: String

    data class TextContent(
      override val role: String,
      val content: String?,
      val toolCallId: String? = null,
      val toolCalls: List<ToolCall>? = null
    ) : Message()

    data class ChunkContent(
      override val role: String,
      val content: List<ContentChunk>
    ) : Message()

    @Serializable
    data class ToolCall(val id: String, val type: String = "function", val function: ToolCallFunction) {
      @Serializable
      data class ToolCallFunction(val name: String, val arguments: String)
    }
  }

  object MistralMessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Mistral.Message")

    override fun serialize(encoder: Encoder, value: Message) {
      val jsonEncoder = encoder as JsonEncoder
      val json = jsonEncoder.json
      val element = when (value) {
        is Message.TextContent -> buildJsonObject {
          put("role", value.role)
          if (value.content != null) {
            put("content", value.content)
          }
          value.toolCallId?.let { put("tool_call_id", it) }
          value.toolCalls?.let { toolCalls ->
            put("tool_calls", json.encodeToJsonElement(toolCalls))
          }
        }

        is Message.ChunkContent -> buildJsonObject {
          put("role", value.role)
          put("content", json.encodeToJsonElement(value.content))
        }
      }
      jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): Message {
      val jsonDecoder = decoder as JsonDecoder
      val obj = jsonDecoder.decodeJsonElement().jsonObject
      val role = obj["role"]!!.jsonPrimitive.content
      val content = obj["content"]?.jsonPrimitive?.contentOrNull
      val toolCallId = obj["tool_call_id"]?.jsonPrimitive?.contentOrNull
      val toolCalls = obj["tool_calls"]?.takeIf { it !is JsonNull }?.let { element ->
        jsonDecoder.json.decodeFromJsonElement<List<Message.ToolCall>>(element)
      }
      return Message.TextContent(role, content, toolCallId, toolCalls)
    }
  }

  @Serializable
  data class Choice(
    val message: Message,
    val index: Int,
    val logprobs: Int? = null,
    @SerialName("finish_reason") val finishReason: String?
  )

  @Serializable
  data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int? = null,
    @SerialName("total_tokens") val totalTokens: Int
  )

  @Serializable
  data class ChatModel(
    val model: String,
    val messages: List<Message>,
    @Transient
    val options: MistralOptions? = null,
    @SerialName("max_tokens")
    val maxTokens: Int = 4096,
    @Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null,
    val tools: List<OpenAi.FunctionDeclaration>? = null,
  ) {

    val temperature: Double? = options?.temperature

    @SerialName("top_p")
    val topP: Double? = options?.topP

    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = options?.frequencyPenalty

    @SerialName("response_format")
    @Serializable
    val responseFormat: ResponseFormat = if (jsonSchemaSpec != null && jsonSchemaSpec.schema["type"] != JsonPrimitive("string")) {
      ResponseFormat.JsonSchemaResponse(jsonSchemaSpec)
    } else {
      ResponseFormat.TextResponse
    }
  }

  @Serializable
  sealed class Response {

    @Serializable
    data class ErrorResponse(val message: String, val type: String, val code: String) : Response()

    @Serializable
    data class NormalResponse(val id: String, val created: Long, val model: String,
                              val choices: List<Choice>,
                              val usage: Usage) : Response()
  }

  object ResponseSerializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
      return when (element.jsonObject["object"]!!.jsonPrimitive.content) {
        "error"           -> Response.ErrorResponse.serializer()
        "chat.completion" -> Response.NormalResponse.serializer()
        else              -> Response.NormalResponse.serializer()
      }
    }
  }
}

fun IFunctionDeclaration.toMistral(): FunctionDeclaration {
  return this.toOpenAi()
}
