/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration


class HedgeChatService(
  private val domainModelService: IDomainModelService,
  private val hedgeConfig: HedgeConfig,
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
    val fallbacks: Set<ProviderModel>,
    val preferredWait: Duration,     // 專指等待 preferred 的時間
  ) {
    init {
      require(!fallbacks.contains(preferred))
    }
  }

  override suspend fun <T> chatComplete(
    serializer: KSerializer<T>,
    messages: List<Msg>,
    user: String?,
    funCalls: Set<IFunctionDeclaration>,
    jsonSchema: JsonSchemaSpec?,
    chatOptionsTemplate: ChatOptions,
    modelTimeout: Duration,
    postProcessors: List<IPostProcessor>,
    locale: Locale
  ): ResultDto<T>? = coroutineScope {

    val allModels = setOf(hedgeConfig.preferred) + hedgeConfig.fallbacks

    val deferredMap: Map<ProviderModel, Deferred<ResultDto<T>?>> = allModels.associateWith { providerModel: ProviderModel ->

      val currentChatOptions = chatOptionsTemplate.copy(
        temperature = providerModel.temperature ?: chatOptionsTemplate.temperature
      )

      async(Dispatchers.IO) {
        val impl = domainModelService.findImpl(providerModel.provider)
        val r = impl.chatComplete(
          model = providerModel.model,
          messages = messages,
          user = user,
          funCalls = funCalls,
          timeout = modelTimeout,
          chatOptions = currentChatOptions,
          jsonSchema = jsonSchema
        )
        r.takeIf { it is Reply.Normal }
          ?.let { it as Reply.Normal }
          ?.let {
            val processed = postProcessors.fold(it.content) { acc, postProcessor ->
              val (processed, _) = postProcessor.process(acc, locale)
              processed
            }

            Triple(processed, it.inputTokens, it.outputTokens)
          }?.let { (string, inputTokens, outputTokens) ->
            try {
              val model: T = json.decodeFromString(serializer, string)
              ResultDto(model, providerModel.provider, providerModel.model, inputTokens, outputTokens)
            } catch (e : SerializationException) {
              null
            }
          }
      }
    }

    val result: ResultDto<T>? = withTimeoutOrNull(hedgeConfig.preferredWait) {
      deferredMap[hedgeConfig.preferred]?.await()
    }

    if (result != null) {
      // preferred 在 delay 內完成	-> 回傳 preferred，並取消其他
      deferredMap.filterKeys { it != hedgeConfig.preferred }.values.forEach { it.cancel() }
      result
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
