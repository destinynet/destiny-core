/**
 * Created by smallufo on 2025-05-13.
 */
package destiny.tools.ai

import destiny.tools.ai.model.FormatSpec
import jakarta.inject.Named
import java.util.*
import kotlin.time.Duration


interface IChatOrchestrator {

  suspend fun <T: Any> chatComplete(
    formatSpec: FormatSpec<out T>,
    messages: List<Msg>,
    postProcessors: List<IPostProcessor>,
    locale: Locale = Locale.getDefault(),
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    chatOptionsTemplate: ChatOptions = ChatOptions()
  ): Reply.Normal<out T>?

  suspend fun <T : Any> chatComplete(
    formatSpec: FormatSpec<out T>,
    message: String,
    postProcessors: List<IPostProcessor>,
    locale: Locale = Locale.getDefault(),
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    chatOptionsTemplate: ChatOptions = ChatOptions()
  ): Reply.Normal<out T>? {
    return chatComplete(formatSpec, listOf(Msg(Role.USER, message)), postProcessors, locale, funCalls, chatOptionsTemplate)
  }

}

interface IChatOrchestratorFactory {
  fun hedged(config: HedgeChatService.HedgeConfig): IChatOrchestrator
  fun resilient(config: ResilientChatService.ResilientConfig): IChatOrchestrator
}

interface IChatConfig {
  val user: String?
  val modelTimeout: Duration
}

@Named
class ChatOrchestratorFactory(
  private val domainModelService: IDomainModelService,
) : IChatOrchestratorFactory {

  override fun hedged(config: HedgeChatService.HedgeConfig): IChatOrchestrator {
    return HedgeChatService(domainModelService, config)
  }

  override fun resilient(config: ResilientChatService.ResilientConfig): IChatOrchestrator {
    return ResilientChatService(domainModelService, config)
  }
}
