package destiny.tools.ai

import java.time.Duration
import java.time.temporal.ChronoUnit

enum class Role {
  USER,
  ASSISTANT
}

data class Msg(val role: Role, val content: String)

interface IChatCompletion {

  suspend fun chatComplete(messages: List<Msg>, user: String? = null, timeout: Duration = Duration.of(60, ChronoUnit.SECONDS)) : String?

}

