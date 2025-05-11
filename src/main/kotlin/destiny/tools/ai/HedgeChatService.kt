/**
 * Created by smallufo on 2025-05-11.
 */
package destiny.tools.ai

import jakarta.inject.Named
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration.Companion.seconds


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
    temperature: Temperature? = null,
    jsonSchema: JsonSchemaSpec? = null,
    locale: Locale
  ): T? = coroutineScope {
    val allModels = setOf(providerGroup.preferred) + providerGroup.fallbacks

    val deferredMap: Map<ProviderModel, Deferred<T?>> = allModels.associateWith { model ->
      async(Dispatchers.IO) {
        val impl = domainModelService.findImpl(model.provider)
        val r = impl.chatComplete(
          model = model.model,
          messages = messages,
          user = user,
          funCalls = funCalls,
          timeout = providerGroup.fallbackDelay + 10.seconds,
          temperature = model.temperature ?: temperature,
          jsonSchema = jsonSchema
        )
        r.takeIf { it is Reply.Normal }
          ?.let { it as Reply.Normal }
          ?.let {
            providerGroup.postProcessors.fold(it.content) { acc, postProcessor ->
              val (processed, _) = postProcessor.process(acc, locale)
              processed
            }
          }?.let { string ->
            json.decodeFromString<T>(string)
          }
      }
    }
    val preferredDeferred: Deferred<T?> = deferredMap[providerGroup.preferred]!!

    val result: T? = withTimeoutOrNull(providerGroup.fallbackDelay) {
      preferredDeferred.await()
    }

    if (result != null) {
      (deferredMap - providerGroup.preferred).values.forEach { it.cancel() }
      result
    } else {
      null
    }
  }
}
