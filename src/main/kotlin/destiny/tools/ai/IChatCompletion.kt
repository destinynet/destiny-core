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

  suspend fun chatComplete(messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = Duration.of(60, ChronoUnit.SECONDS)) : Reply

  suspend fun chatComplete(messages: List<Msg>, user: String? = null, funCall: IFunctionDeclaration, timeout: Duration = Duration.of(60, ChronoUnit.SECONDS)) : Reply {
    return chatComplete(messages, user, setOf(funCall), timeout)
  }
}

abstract class AbstractChatCompletion : IChatCompletion {

  abstract suspend fun doChatComplete(messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration): Reply

  override suspend fun chatComplete(messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration): Reply {
    val filteredFunCalls = funCalls.filter { it.applied(messages) }.toSet()
    return doChatComplete(messages, user, filteredFunCalls, timeout)
  }
}
