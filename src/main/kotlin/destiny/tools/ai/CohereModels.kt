/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class Cohere {

  @Serializable
  data class Content(val type: String = "text", val text: String)


  @Serializable
  data class Message(val role: String, val content: List<Content>) {
    constructor(role: String, text: String) : this(role, listOf(Content("text", text)))
  }

  @Serializable
  data class Request(val model: String, val messages: List<Message>, val stream: Boolean = false)

  @Serializable
  data class Response(
    val id: String,
    @SerialName("finish_reason")
    val finishReason: String,
    val message: Message,
  )
}
