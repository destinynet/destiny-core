package destiny.tools.ai

import mu.KotlinLogging
import java.time.Duration
import java.time.temporal.ChronoUnit

sealed class Reply(val provider: String) {

  data class Normal(val content: String, val p: String, val model: String) : Reply(p)

  sealed class Error(p: String) : Reply(p) {

    data class TooLong(val message: String, val p: String) : Error(p)

    sealed class Unrecoverable(p: String) : Error(p) {
      data class InvalidApiKey(val p: String) : Unrecoverable(p)
      data class Busy(val p: String) : Unrecoverable(p)
      data class Unknown(val message: String, val p: String) : Unrecoverable(p)
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

    val finalMsgs = messages.fold(mutableListOf<Msg>()) { acc, msg ->
      if (acc.isNotEmpty()) {
        val lastMsg = acc.last()
        if (lastMsg.role == msg.role) {
          if (lastMsg.stringContents == msg.stringContents) {
            // Drop the duplicate message
            logger.warn { "DROP_DUPLICATED  : ${msg.stringContents}" }
          } else {
            // Append the content
            acc[acc.size - 1] = lastMsg.copy(contents = buildList {
              addAll(lastMsg.contents)
              msg.contents
            })
          }
        } else {
          acc.add(msg)
        }
      } else {
        acc.add(msg)
      }
      acc
    }

    return doChatComplete(finalMsgs, user, filteredFunCalls, timeout)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
