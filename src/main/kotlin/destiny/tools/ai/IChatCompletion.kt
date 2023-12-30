package destiny.tools.ai

import java.time.Duration
import java.time.temporal.ChronoUnit

sealed class Reply {

  data class Normal(val content: String, val provider: String, val model: String) : Reply()

  sealed class Error : Reply() {

    data class TooLong(val message: String) : Error()

    sealed class Unrecoverable : Error() {
      data object InvalidApiKey : Unrecoverable()
      data object Busy : Unrecoverable()
      data class Unknown(val message: String) : Unrecoverable()
    }
  }
}


interface IChatCompletion {

  suspend fun chatComplete(messages: List<Msg>, user: String? = null, timeout: Duration = Duration.of(60, ChronoUnit.SECONDS)) : Reply


}

