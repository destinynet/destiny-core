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

    val deferredMap: Map<ProviderModel, Deferred<Reply<T>?>> = allModels.associateWith { providerModel: ProviderModel ->

      val currentChatOptions = chatOptionsTemplate.copy(
        temperature = providerModel.temperature ?: chatOptionsTemplate.temperature
      )

      async(Dispatchers.IO + CoroutineName("ChatCompletion-${providerModel.provider}/${providerModel.model}")) {
        val impl = domainModelService.findImpl(providerModel.provider)
        impl.typedChatComplete(providerModel.model, messages, formatSpec, json, locale, currentChatOptions, postProcessors, config.user, funCalls, config.modelTimeout)
      }
    }

    val preferredResult: Reply<T>? = withTimeoutOrNull(config.preferredWait) {
      deferredMap[config.preferred]?.await()
    }

    if (preferredResult is Reply.Normal) {
      // preferred 在時限內成功完成 -> 回傳 preferred，並取消其他
      logger.info { "Preferred model ${config.preferred} succeeded within the time limit." }
      deferredMap.filterKeys { it != config.preferred }.values.forEach { it.cancel() }
      preferredResult
    } else {
      // preferred 超時、失敗(Error)或回傳null -> 從 fallbacks 中選擇第一個成功的
      if (preferredResult != null) {
        logger.warn { "Preferred model ${config.preferred} failed or returned an error: $preferredResult. Looking for a fallback..." }
      } else {
        logger.warn { "Preferred model ${config.preferred} timed out. Looking for a fallback..." }
      }

      // 4. 只在 fallbacks 中 select，不再包含已經失敗的 preferred
      val fallbackDeferreds = deferredMap.filterKeys { it in config.fallbacks }


      select {
        fallbackDeferreds.forEach { (model, deferred) ->
          deferred.onAwait { res ->
            // 只選擇成功的 Normal 結果
            if (res is Reply.Normal) {
              model to res
            } else {
              // 忽略 Error 或 null 的結果
              null
            }
          }
        }
      }?.also { (winnerModel, _) ->
        logger.info { "Using fallback result from $winnerModel" }
        // 取消所有其他仍在執行的任務
        deferredMap.filterKeys { it != winnerModel }.values.forEach { if (it.isActive) it.cancel() }
      }?.second
    }
  }

  companion object {
    val logger = KotlinLogging.logger {}
  }
}
