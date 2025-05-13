/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import jakarta.inject.Named
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.*


@Named
class HedgeChatService(val domainModelService: IDomainModelService) {

  @OptIn(ExperimentalSerializationApi::class)
  val json: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    allowTrailingComma = true
    isLenient = true
  }

  suspend inline fun <reified T> hedgedChatComplete(
    providerGroup: ProviderGroup,
    messages: List<Msg>,
    user: String? = null,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    jsonSchema: JsonSchemaSpec? = null,
    locale: Locale
  ): ResultDto<T>? = coroutineScope {
    val allModels = setOf(providerGroup.preferred) + providerGroup.fallbacks

    val deferredMap: Map<ProviderModel, Deferred<ResultDto<T>?>> = allModels.associateWith { providerModel: ProviderModel ->
      async(Dispatchers.IO) {
        val impl = domainModelService.findImpl(providerModel.provider)
        val r = impl.chatComplete(
          model = providerModel.model,
          messages = messages,
          user = user,
          funCalls = funCalls,
          timeout = providerGroup.modelTimeout,
          chatOptions = ChatOptions(providerModel.temperature),
          jsonSchema = jsonSchema
        )
        r.takeIf { it is Reply.Normal }
          ?.let { it as Reply.Normal }
          ?.let {
            val processed = providerGroup.postProcessors.fold(it.content) { acc, postProcessor ->
              val (processed, _) = postProcessor.process(acc, locale)
              processed
            }

            Triple(processed, it.inputTokens, it.outputTokens)
          }?.let { (string, inputTokens, outputTokens) ->
            try {
              ResultDto(json.decodeFromString<T>(string), providerModel.provider, providerModel.model, inputTokens, outputTokens)
            } catch (e : SerializationException) {
              null
            }
          }
      }
    }

    val result: ResultDto<T>? = withTimeoutOrNull(providerGroup.preferredWait) {
      deferredMap[providerGroup.preferred]!!.await()
    }

    if (result != null) {
      // preferred 在 delay 內完成	-> 回傳 preferred，並取消其他
      deferredMap.filterKeys { it != providerGroup.preferred }.values.forEach { it.cancel() }
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
