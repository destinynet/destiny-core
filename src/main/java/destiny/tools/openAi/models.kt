package destiny.tools.openAi

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletionRequest(val prompt: String,
                             val user : String? = null,
                             val model : String = "text-davinci-003",
                             @SerialName("max_tokens")
                             val maxTokens : Int = 2048,
                             val temperature: Double = 1.0,
                             val n : Int = 1,
                             val stream: Boolean = false)


@Serializable
data class Usage(@SerialName("prompt_tokens")
                 val promptTokens: Int,
                 @SerialName("completion_tokens")
                 val completionTokens: Int? = null,
                 @SerialName("total_tokens")
                 val totalTokens: Int)

@Serializable
data class Choice(val text: String,
                  val index: Int,
                  val logprobs: Int?,
                  @SerialName("finish_reason")
                  val finishReason: String?)

@Serializable
data class CompletionResult(val id: String,
                            /** always 'text_completion' */
                            val `object`: String,
                            val created: Long,
                            /** maybe 'text-davinci-003' */
                            val model: String,
                            val choices: List<Choice>,
                            val usage: Usage
)

@Serializable
data class Error(val message: String,
                 val type: String,
                 @Contextual
                 val param: Any? = null,
                 @Contextual
                 val code: String? = null)

sealed class OpenAiReply {
  data class Normal(val content: String) : OpenAiReply()

  sealed class Error : OpenAiReply() {

    // recoverable
    data class TooLong(val message: String) : Error()

    sealed class Unrecoverable : Error() {
      object InvalidApiKey : Unrecoverable()
      object Busy : Unrecoverable()
      data class Unknown(val message: String) : Unrecoverable()
    }
  }

}

