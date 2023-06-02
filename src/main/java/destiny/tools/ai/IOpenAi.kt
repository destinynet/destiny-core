package destiny.tools.ai

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.temporal.ChronoUnit


interface IOpenAi : IChatCompletion {

  enum class Role {
    user , system , assistant
  }

  @Serializable
  data class Msg(val role: Role, val content: String) : java.io.Serializable

  suspend fun chatComplete(messages: List<Msg>, user: String? = null, timeout : Duration = Duration.of(60, ChronoUnit.SECONDS)): OpenAiReply

  suspend fun textComplete(prompt: String, user: String? = null): OpenAiReply

  override suspend fun chatCompletion(messages: List<destiny.tools.ai.Msg>, user: String?, timeout: Duration): String? {

    val mList = messages.map { msg ->
      Msg(
        role = when(msg.role) {
          destiny.tools.ai.Role.USER -> Role.user
          destiny.tools.ai.Role.ASSISTANT -> Role.assistant
        },
        content = msg.content
      )
    }
    return when(val reply = chatComplete(mList, user, timeout)) {
      is OpenAiReply.Normal -> reply.content
      else                  -> null
    }
  }
}
