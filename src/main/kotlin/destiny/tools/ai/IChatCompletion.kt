package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.model.FormatSpec
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class Reply<out T> {

  abstract val provider: Provider

  data class Normal<T>(val content: T,
                       val think: String?,
                       override val provider: Provider,
                       val model: String,
                       val invokedFunCalls: List<FunCall> = emptyList(),
                       val inputTokens: Int? = null,
                       val outputTokens: Int? = null,
                       val duration: Duration? = null) : Reply<T>()

  sealed class Error : Reply<Nothing>() {

    data class TooLong(val message: String, override val provider: Provider) : Error()

    data class DeserializationFailure(val errorMessage: String,
                                      val originalContent: String,
                                      override val provider: Provider,
                                      val model: String) : Error()

    data class InvalidApiKey(override val provider: Provider) : Error()
    data class Busy(override val provider: Provider) : Error()
    data class Unknown(val message: String, override val provider: Provider) : Error()

  }
}


interface IChatCompletion {

  val provider: Provider

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = 90.seconds, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec? = null) : Reply<String>

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCall: IFunctionDeclaration, timeout: Duration = 90.seconds, chatOptions: ChatOptions) : Reply<String> {
    return chatComplete(model, messages, user, setOf(funCall), timeout, chatOptions)
  }

  suspend fun <T : Any> typedChatComplete(
    model: String,
    messages: List<Msg>,
    formatSpec: FormatSpec<T>,
    json: Json,
    locale: Locale = Locale.getDefault(),
    chatOptions: ChatOptions = ChatOptions(),
    postProcessors: List<IPostProcessor> = emptyList(),
    user: String? = null,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    timeout: Duration = 90.seconds
  ) : Reply<T>?

  suspend fun <T : Any> typedChatComplete(
    model: String,
    message: String,
    formatSpec: FormatSpec<T>,
    json: Json,
    locale: Locale = Locale.getDefault(),
    chatOptions: ChatOptions = ChatOptions(),
    postProcessors: List<IPostProcessor> = emptyList(),
    user: String? = null,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    timeout: Duration = 90.seconds
  ): Reply<T>? {
    return typedChatComplete(model, listOf(Msg(Role.USER, message)), formatSpec, json, locale, chatOptions, postProcessors, user, funCalls, timeout)
  }
}

abstract class AbstractChatCompletion : IChatCompletion {

  abstract suspend fun doChatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec? = null): Reply<String>

  override suspend fun chatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec?): Reply<String> {
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

    return doChatComplete(model, finalMsgs, user, filteredFunCalls, timeout, chatOptions, jsonSchema)
  }

  @Suppress("UNCHECKED_CAST")
  override suspend fun <T : Any> typedChatComplete(
    model: String,
    messages: List<Msg>,
    formatSpec: FormatSpec<T>,
    json: Json,
    locale: Locale,
    chatOptions: ChatOptions,
    postProcessors: List<IPostProcessor>,
    user: String?,
    funCalls: Set<IFunctionDeclaration>,
    timeout: Duration
  ): Reply<T>? {

    return when (val rawReply: Reply<String> = chatComplete(model, messages, user, funCalls, timeout, chatOptions, formatSpec.jsonSchema)) {
      is Reply.Normal<String> -> {
        val processedString = postProcessors.fold(rawReply.content) { currentContent, postProcessor ->
          val (nextContent, _) = postProcessor.process(currentContent, locale)
          nextContent
        }
        val serializer = formatSpec.serializer

        val typedResult: T = try {
          if (formatSpec.kClass == String::class) {
            processedString as T
          } else {
            val parsed = json.decodeFromString(serializer, processedString)
            if (parsed is IProviderModel) {
              (parsed as IProviderModel).withProviderModel(provider, model) as T
            } else {
              parsed
            }
          }
        } catch (e: SerializationException) {
          logger.warn(e) { "Failed to deserialize content from $model (serializer: ${serializer.descriptor.serialName}). Content: $processedString" }
          // 反序列化失敗，返回一個特定的錯誤類型
          return Reply.Error.DeserializationFailure(
            errorMessage = e.localizedMessage ?: "Serialization failed",
            originalContent = processedString,
            provider = rawReply.provider,
            model = rawReply.model
          )
        } catch (e: ClassCastException) { // 處理 String as T 可能的錯誤
          logger.warn(e) { "Failed to cast processed string to T for $model. Expected String but T is not String. Content: $processedString" }
          return Reply.Error.DeserializationFailure(
            errorMessage = "Type cast failed: Expected String, but T is ${serializer.descriptor.serialName}",
            originalContent = processedString,
            provider = rawReply.provider,
            model = rawReply.model
          )
        }

        Reply.Normal(
          content = typedResult,
          think = rawReply.think,
          provider = rawReply.provider,
          model = rawReply.model,
          invokedFunCalls = rawReply.invokedFunCalls,
          inputTokens = rawReply.inputTokens,
          outputTokens = rawReply.outputTokens,
          duration = rawReply.duration
        )
      }

      is Reply.Error -> rawReply
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
