/**
 * Created by smallufo on 2025-05-13.
 */
package destiny.tools.ai

import jakarta.inject.Named
import kotlinx.serialization.KSerializer
import java.util.*
import kotlin.time.Duration


interface IChatOrchestrator {

  suspend fun <T: Any> chatComplete(
    serializer: KSerializer<T>,
    messages: List<Msg>,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    jsonSchema: JsonSchemaSpec? = null,
    chatOptionsTemplate: ChatOptions, // 提供一個聊天選項模板
    postProcessors: List<IPostProcessor>,
    locale: Locale
  ): Reply.Normal<T>?

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
