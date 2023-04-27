package destiny.tools.openAi

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.temporal.ChronoUnit


interface IOpenAi {

  enum class Role {
    user , system , assistant
  }

  @Serializable
  data class Msg(val role: Role, val content: String) : java.io.Serializable

  suspend fun chatComplete(messages: List<Msg>, user: String? = null , timeout : Duration = Duration.of(60 , ChronoUnit.SECONDS)): OpenAiReply

  suspend fun textComplete(prompt: String, user: String? = null): OpenAiReply
}
