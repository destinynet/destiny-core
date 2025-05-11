package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class Reply {

  abstract val provider: String

  data class Normal(val content: String,
                    override val provider: String,
                    val model: String,
                    val invokedFunCalls: List<FunCall> = emptyList(),
                    val inputTokens: Int? = null,
                    val outputTokens: Int? = null,
                    val duration: Duration? = null,
  ) : Reply()

  sealed class Error : Reply() {

    data class TooLong(val message: String, override val provider: String) : Error()

    sealed class Unrecoverable : Error() {
      data class InvalidApiKey(override val provider: String) : Unrecoverable()
      data class Busy(override val provider: String) : Unrecoverable()
      data class Unknown(val message: String, override val provider: String) : Unrecoverable()
    }
  }
}


interface IChatCompletion {

  val provider: String

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = 90.seconds, temperature: Temperature?, jsonSchema: JsonSchemaSpec? = null) : Reply

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCall: IFunctionDeclaration, timeout: Duration = 90.seconds, temperature: Temperature?) : Reply {
    return chatComplete(model, messages, user, setOf(funCall), timeout, temperature)
  }
}

abstract class AbstractChatCompletion : IChatCompletion {

  @OptIn(ExperimentalSerializationApi::class)
  val json: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    allowTrailingComma = true
    isLenient = true
  }

  abstract suspend fun doChatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, temperature: Temperature?, jsonSchema: JsonSchemaSpec? = null): Reply

  override suspend fun chatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, temperature: Temperature?, jsonSchema: JsonSchemaSpec?): Reply {
    val filteredFunCalls = funCalls.filter { it.applied(messages) }.toSet()

    val finalMsgs = messages.fold(mutableListOf<Msg>()) { acc, msg ->
      if (acc.isNotEmpty()) {
        val lastMsg = acc.last()
        if (lastMsg.role == msg.role) {
          if (lastMsg.stringContents == msg.stringContents) {
            // Drop the duplicate message
            logger.warn { "DROP_DUPLICATED  : ${msg.stringContents}" }
          } else {
            // Append the content
            acc[acc.size - 1] = lastMsg.copy(contents = buildList {
              addAll(lastMsg.contents)
              addAll(msg.contents)
            })
          }
        } else {
          acc.add(msg)
        }
      } else {
        acc.add(msg)
      }
      acc
    }

    val funCallPrompts = (filteredFunCalls.joinToString(",") { it.name }).let {
      if (it.isEmpty()) it
      else buildString {
        // TODO : add meta data , to enhance LLM memory
        append("With function calls if applicable : ")
        append(it)
      }
    }

    if (filteredFunCalls.isNotEmpty() && finalMsgs.isNotEmpty()) {
      val lastMsg = finalMsgs.last()
      finalMsgs[finalMsgs.lastIndex] = lastMsg.copy(
        contents = buildList {
          addAll(lastMsg.contents)
          add(Content.StringContent("\n$funCallPrompts"))
        }
      )
    }

    return doChatComplete(model, finalMsgs, user, filteredFunCalls, timeout, temperature, jsonSchema)
  }

  suspend inline fun <reified T> hedgedChatComplete(
    domainModelService : IDomainModelService,
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

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
