package destiny.tools.ai

import destiny.tools.KotlinLogging
import java.time.Duration
import java.time.temporal.ChronoUnit

sealed class Reply {

  abstract val provider: String

  data class Normal(val content: String, override val provider: String, val model: String) : Reply()

  sealed class Error : Reply() {

    data class TooLong(val message: String, override val provider: String) : Error()

    sealed class Unrecoverable : Error() {
      data class InvalidApiKey(override val provider: String) : Unrecoverable()
      data class Busy(override val provider: String) : Unrecoverable()
      data class Unknown(val message: String, override val provider: String) : Unrecoverable()
    }
  }
}


interface IChatCompletion {

  val provider: String

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = Duration.of(60, ChronoUnit.SECONDS)) : Reply

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCall: IFunctionDeclaration, timeout: Duration = Duration.of(60, ChronoUnit.SECONDS)) : Reply {
    return chatComplete(model, messages, user, setOf(funCall), timeout)
  }
}

abstract class AbstractChatCompletion : IChatCompletion {

  abstract suspend fun doChatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration): Reply

  override suspend fun chatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration): Reply {
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
              addAll(msg.contents)
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

    val funCallPrompts = (filteredFunCalls.joinToString(",") { it.name }).let {
      if (it.isEmpty()) it
      else buildString {
        append("With function calls if applicable : ")
        append(it)
      }
    }

    if (filteredFunCalls.isNotEmpty() && finalMsgs.isNotEmpty()) {
      val lastMsg = finalMsgs.last()
      finalMsgs[finalMsgs.lastIndex] = lastMsg.copy(
        contents = buildList {
          addAll(lastMsg.contents)
          add(Content.StringContent("\n$funCallPrompts"))
        }
      )
    }

    return doChatComplete(model, finalMsgs, user, filteredFunCalls, timeout)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
