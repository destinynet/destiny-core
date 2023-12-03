package destiny.tools.ai

import kotlinx.serialization.Serializable


interface IOpenAi : IChatCompletion {

  @Serializable
  data class OpenAiMsg(val role: String, val content: String)
}
