package destiny.tools.ai.llm

import destiny.tools.ai.ChatOptions
import destiny.tools.ai.IFunctionDeclaration
import destiny.tools.ai.InputSchema
import destiny.tools.ai.JsonSchemaSpec
import destiny.tools.ai.model.ResponseFormat
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

class OpenAi {

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

  @Serializable(with = OpenAiMessageSerializer::class)
  sealed class Message {
    abstract val role: String

    data class TextContent(
      override val role: String,
      val content: String?,
      val reasoning: String? = null,
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

  object OpenAiMessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenAiMessage") {
      element<String>("role")
      element<JsonElement>("content")
      element<String>("reasoning_content", isOptional = true)
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
          value.reasoning?.let { put("reasoning_content", it) }
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
      val reasoning = jsonObject["reasoning_content"]?.jsonPrimitive?.contentOrNull
      val toolCallId = jsonObject["tool_call_id"]?.jsonPrimitive?.contentOrNull
      val toolCalls = jsonObject["tool_calls"]?.let {
        if (it is JsonNull) null else json.decodeFromJsonElement<List<Message.ToolCall>>(it)
      }
      return Message.TextContent(role, content, reasoning, toolCallId, toolCalls)
    }
  }

  @Serializable
  sealed class Response {

    @Serializable
    @SerialName("error")
    data class ErrorResponse(val error: Error) : Response() {
      @Serializable
      data class Error(val message: String, val type: String, val code: String?)
    }

    @Serializable
    data class NormalResponse(val id: String,
                              /** always 'chat.completion' */
                              val `object`: String,
                              val created: Long,
                              val model: String,
                              val choices: List<Choice>,
                              val usage: Usage): Response() {
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
    }
  }

  object ResponseSerializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
      return when {
        element.jsonObject.containsKey("error") -> Response.ErrorResponse.serializer()
        else                                    -> Response.NormalResponse.serializer()
      }
    }
  }

  @Serializable
  data class FunctionDeclaration(val type: String, val function: Function) {
    @Serializable
    data class Function(val name: String, val description: String, val parameters: InputSchema)
  }

  data class OpenAiOptions(
    val temperature: Double? = null,      // 0.0 ~ 2.0
    val topP: Double? = null,             // 0.0 ~ 1.0
    val frequencyPenalty: Double? = null  // -2.0 ~ 2.0
  ) {
    companion object {
      fun ChatOptions.toOpenAi(): OpenAiOptions {
        return OpenAiOptions(
          this.temperature?.value?.let { it * 2 },
          this.topP?.value,
          this.frequencyPenalty?.value?.let { it * 2 }
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val messages: List<Message>,
    val user: String?,
    val model: String,
    @Transient
    val options: OpenAiOptions? = null,
    /**
     * to explicitly control the total number of tokens generated by the model, including both reasoning and visible completion tokens.
     * OpenAI recommends reserving at least 25,000 tokens for reasoning and outputs when you start experimenting with these models.
     */
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,
    val tools: List<FunctionDeclaration>? = null,
    @Transient
    val jsonSchemaSpec: JsonSchemaSpec? = null
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
}

fun IFunctionDeclaration.toOpenAi(): OpenAi.FunctionDeclaration {
  return OpenAi.FunctionDeclaration(
    "function",
    OpenAi.FunctionDeclaration.Function(
      this.name, this.description,
      InputSchema(
        "object",
        this.parameters.associate { p -> p.name to InputSchema.Property(p.type, p.description, null) },
        this.parameters.filter { it.required }.map { it.name }.toList()
      )
    )
  )
}
