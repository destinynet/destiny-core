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
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
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

  override suspend fun <T> chatComplete(
    serializer: KSerializer<T>,
    messages: List<Msg>,
    funCalls: Set<IFunctionDeclaration>,
    jsonSchema: JsonSchemaSpec?,
    chatOptionsTemplate: ChatOptions,
    postProcessors: List<IPostProcessor>,
    locale: Locale
  ): ResultDto<T>? = coroutineScope {

    val allModels = setOf(config.preferred) + config.fallbacks

    val deferredMap: Map<ProviderModel, Deferred<ResultDto<T>?>> = allModels.associateWith { providerModel: ProviderModel ->

      val currentChatOptions = chatOptionsTemplate.copy(
        temperature = providerModel.temperature ?: chatOptionsTemplate.temperature
      )

      async(Dispatchers.IO + CoroutineName("ChatCompletion-${providerModel.provider}/${providerModel.model}")) {
        val impl = domainModelService.findImpl(providerModel.provider)
        val r = impl.chatComplete(providerModel.model, messages,config.user, funCalls, config.modelTimeout, currentChatOptions, jsonSchema)
        r.takeIf { it is Reply.Normal }
          ?.let { it as Reply.Normal }
          ?.let {
            val processedString = postProcessors.fold(it.content) { acc, postProcessor ->
              val (processed, _) = postProcessor.process(acc, locale)
              processed
            }

            val resultData: T = if (serializer.descriptor.kind == PrimitiveKind.STRING && serializer == String.serializer()) {
              logger.debug { "Serializer for ${providerModel.model} is String.serializer(), bypassing JSON deserialization." }
              @Suppress("UNCHECKED_CAST") // Safe due to the serializer check
              processedString as T
            } else {
              try {
                json.decodeFromString(serializer, processedString)
              } catch (e: SerializationException) {
                logger.warn(e) { "Failed to deserialize content from ${providerModel.model} (serializer: ${serializer.descriptor.serialName}). Content: $processedString" }
                return@async null // This attempt failed for this model
              }
            }

            ResultDto(resultData, it.think, it.provider, it.model, it.invokedFunCalls, it.inputTokens, it.outputTokens, it.duration)
          }
      }
    }

    val result: ResultDto<T>? = withTimeoutOrNull(config.preferredWait) {
      deferredMap[config.preferred]?.await()
    }

    if (result != null) {
      // preferred 在 delay 內完成	-> 回傳 preferred，並取消其他
      deferredMap.filterKeys { it != config.preferred }.values.forEach { it.cancel() }
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
