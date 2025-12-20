package destiny.tools.ai

import jakarta.inject.Named

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
