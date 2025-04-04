/**
 * Created by smallufo on 2025-01-01.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Groq {

  @Serializable
  data class Message(val role: String, val content: String)

  @Serializable
  data class Response(val id: String, val model: String, val choices: List<Choice>) {
    @Serializable
    data class Choice(
      val index: Int, val message: Message, @SerialName("finish_reason") val finishReason: String
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

  @Serializable
  data class ChatModel(val model: String, val messages: List<Message>, val user: String?, val temperature: Double = 1.0,
                       @kotlinx.serialization.Transient
                       val jsonSchemaSpec: JsonSchemaSpec? = null) {

    @SerialName("response_format")
    val responseFormat: ResponseFormat = if (jsonSchemaSpec == null) {
      ResponseFormat.Text
    } else {
      ResponseFormat.JsonObject
    }
  }


}
