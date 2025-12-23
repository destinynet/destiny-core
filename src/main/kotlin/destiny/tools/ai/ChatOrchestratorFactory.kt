package destiny.tools.ai

import jakarta.inject.Named

@Named
class ChatOrchestratorFactory : IChatOrchestratorFactory {

  override fun hedged(config: HedgeChatService.HedgeConfig): IChatOrchestrator {
    return HedgeChatService(config)
  }

  override fun resilient(config: ResilientChatService.ResilientConfig): IChatOrchestrator {
    return ResilientChatService(config)
  }
}
