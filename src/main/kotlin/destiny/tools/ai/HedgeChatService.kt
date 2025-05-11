/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import jakarta.inject.Named
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
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

  data class ResultDto<T>(val result: T , val inputTokens: Int? , val outputTokens: Int?)

  suspend inline fun <reified T> hedgedChatComplete(
    providerGroup: ProviderGroup,
    messages: List<Msg>,
    user: String? = null,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    jsonSchema: JsonSchemaSpec? = null,
    locale: Locale
  ): ResultDto<T>? = coroutineScope {
    val allModels = setOf(providerGroup.preferred) + providerGroup.fallbacks

    val deferredMap: Map<ProviderModel, Deferred<ResultDto<T>?>> = allModels.associateWith { model ->
      async(Dispatchers.IO) {
        val impl = domainModelService.findImpl(model.provider)
        val r = impl.chatComplete(
          model = model.model,
          messages = messages,
          user = user,
          funCalls = funCalls,
          timeout = providerGroup.modelTimeout,
          temperature = model.temperature,
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
            ResultDto(json.decodeFromString<T>(string) , inputTokens, outputTokens)
          }
      }
    }
    val preferredDeferred = deferredMap[providerGroup.preferred]!!

    val result: ResultDto<T>? = withTimeoutOrNull(providerGroup.preferredWait) {
      preferredDeferred.await()
    }

    if (result != null) {
      // preferred 在 delay 內完成	-> 回傳 preferred，並取消其他
      (deferredMap - providerGroup.preferred).values.forEach { it.cancel() }
      result
    } else {
      // preferred 超時但 fallback 有完成	-> 回傳第一個成功 fallback
      (deferredMap - providerGroup.preferred).values.firstNotNullOfOrNull { deferred ->
        runCatching { deferred.await() }.getOrNull()
      }
    }
  }
}
