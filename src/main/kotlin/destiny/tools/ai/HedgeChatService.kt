/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.model.FormatSpec
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration


class HedgeChatService(
  private val domainModelService: IDomainModelService,
  private val config: HedgeConfig,
) : IChatOrchestrator {

  @OptIn(ExperimentalSerializationApi::class)
  val json: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    allowTrailingComma = true
    isLenient = true
  }

  data class HedgeConfig(
    val preferred: ProviderModel,
    val preferredWait: Duration,  // preferred model 若能在此段時間內有結果，則優先回傳
    val fallbacks: Set<ProviderModel>,
    override val modelTimeout: Duration,
    override val user: String?
  ) : IChatConfig {
    init {
      require(!fallbacks.contains(preferred))
      require(modelTimeout > preferredWait)
    }
  }

  override suspend fun <T : Any> chatComplete(
    formatSpec: FormatSpec<out T>,
    messages: List<Msg>,
    funCalls: Set<IFunctionDeclaration>,
    chatOptionsTemplate: ChatOptions,
    postProcessors: List<IPostProcessor>,
    locale: Locale
  ): Reply.Normal<out T>? = coroutineScope {

    val allModels = setOf(config.preferred) + config.fallbacks

    val deferredMap: Map<ProviderModel, Deferred<Reply.Normal<T>?>> = allModels.associateWith { providerModel: ProviderModel ->

      val currentChatOptions = chatOptionsTemplate.copy(
        temperature = providerModel.temperature ?: chatOptionsTemplate.temperature
      )

      async(Dispatchers.IO + CoroutineName("ChatCompletion-${providerModel.provider}/${providerModel.model}")) {
        val impl = domainModelService.findImpl(providerModel.provider)
        impl.typedChatComplete(providerModel.model, messages, config.user, funCalls, config.modelTimeout, currentChatOptions, postProcessors, formatSpec, locale, json)?.let { r ->
          when(r) {
            is Reply.Normal<*> -> r as Reply.Normal<T>
            else -> null
          }
        }
      }
    }

    val preferredResult: Reply.Normal<T>? = withTimeoutOrNull(config.preferredWait) {
      deferredMap[config.preferred]?.await()
    }

    if (preferredResult != null) {
      // preferred 在 delay 內完成	-> 回傳 preferred，並取消其他
      logger.info { "preferredResult != null" }
      deferredMap.filterKeys { it != config.preferred }.values.forEach { it.cancel() }
      preferredResult
    } else {
      // preferred 超時但 fallback 有完成	-> 回傳第一個成功 fallback
      select {
        deferredMap.forEach { (model, deferred) ->
          deferred.onAwait { res -> res?.let { model to it } }
        }
      }?.also { (winnerModel: ProviderModel, _) ->
        logger.info { "Using fallback result from $winnerModel" }
        deferredMap.filterKeys { it != winnerModel }.values.forEach { it.cancel() }
      }?.second
    }
  }

  companion object {
    val logger = KotlinLogging.logger {}
  }
}
