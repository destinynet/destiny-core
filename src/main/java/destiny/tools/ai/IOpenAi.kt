package destiny.tools.ai

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.temporal.ChronoUnit


interface IOpenAi : IChatCompletion {

  @Serializable
  data class OpenAiMsg(val role: String, val content: String)

  suspend fun chatComplete(messages: List<Msg>, user: String? = null, timeout : Duration = Duration.of(60, ChronoUnit.SECONDS)): Reply

  suspend fun textComplete(prompt: String, user: String? = null): Reply

  override suspend fun chatCompletion(messages: List<Msg>, user: String?, timeout: Duration): Reply {

    return when(val reply = chatComplete(messages, user, timeout)) {
      is Reply.Normal -> Reply.Normal(reply.content)
      else            -> Reply.Error.Unrecoverable.Unknown("unknown , TODO")
    }
  }
}
