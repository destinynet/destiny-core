package destiny.tools.ai

import kotlinx.serialization.Serializable


interface IOpenAi : IChatCompletion {

  @Serializable
  data class OpenAiMsg(val role: String, val content: String)

  //suspend fun chatComplete(messages: List<Msg>, user: String? = null, timeout : Duration = Duration.of(60, ChronoUnit.SECONDS)): Reply

  suspend fun textComplete(prompt: String, user: String? = null): Reply
}
