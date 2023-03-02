package destiny.tools.openAi

import kotlinx.serialization.Serializable


interface IOpenAi {

  enum class Role {
    user , system , assistant
  }

  @Serializable
  data class Msg(val role: Role, val content: String)

  suspend fun chatComplete(messages: List<Msg>, user: String? = null): OpenAiReply

  suspend fun textComplete(prompt: String, user: String? = null): OpenAiReply
}
