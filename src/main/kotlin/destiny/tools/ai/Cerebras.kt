/**
 * Created by smallufo on 2025-05-16.
 */
package destiny.tools.ai

import destiny.tools.ai.OpenAi.FunctionDeclaration
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


class Cerebras {

  @Serializable(with = ResponseSerializer::class)
  sealed class Response {
    @Serializable
    data class NormalResponse(
      val id: String,
      val choices: List<OpenAi.Response.NormalResponse.Choice>,
      val created: Long,
      val model: String,
      val `object`: String,
      val usage: OpenAi.Response.NormalResponse.Usage
    ) : Response()

    @Serializable
    data class ErrorResponse(val message: String, val type: String, val param: String, val code: String) : Response()
  }

  object ResponseSerializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
      val obj = element.jsonObject
      return when {
        "choices" in obj && "id" in obj   -> Response.NormalResponse.serializer()
        "message" in obj && "code" in obj -> Response.ErrorResponse.serializer()
        else                              -> {
          throw SerializationException("Unknown response: $element")
        }
      }
    }
  }


  data class CerebrasOptions(
    /** 0 to 1.5 */
    val temperature: Double?,
    /** 0 .. 1 */
    val topP: Double?,

    val seed: Int? = null,
  ) {
    companion object {
      fun ChatOptions.toCerebras(): CerebrasOptions {
        return CerebrasOptions(
          this.temperature?.value?.let { it * 1.5 },
          this.topP?.value
        )
      }
    }
  }

  @Serializable
  data class ChatModel(
    val messages: List<OpenAi.Message>,
    val user: String?,
    val model: String,
    @Transient
    val options: CerebrasOptions? = null,
    val tools: List<FunctionDeclaration>? = null,
  ) {
    val temperature: Double? = options?.temperature

    @SerialName("top_p")
    val topP: Double? = options?.topP

    val seed = options?.seed
  }
}
