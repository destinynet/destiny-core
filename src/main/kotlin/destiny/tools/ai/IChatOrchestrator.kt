/**
 * Created by smallufo on 2025-05-13.
 */
package destiny.tools.ai

import jakarta.inject.Named
import kotlinx.serialization.KSerializer
import java.util.*
import kotlin.time.Duration


interface IChatOrchestrator {

  suspend fun <T> chatComplete(
    serializer: KSerializer<T>,
    messages: List<Msg>,
    user: String? = null,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    jsonSchema: JsonSchemaSpec? = null,
    chatOptionsTemplate: ChatOptions, // 提供一個聊天選項模板
    modelTimeout: Duration,
    postProcessors: List<IPostProcessor>,
    locale: Locale
  ): ResultDto<T>?

}

interface IChatOrchestratorFactory {
  fun hedged(config: HedgeChatService.HedgeConfig): IChatOrchestrator
  fun resilient(config: ResilientChatService.ResilientConfig): IChatOrchestrator
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
