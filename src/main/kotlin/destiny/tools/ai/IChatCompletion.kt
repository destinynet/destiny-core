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
                       val duration: Duration? = null,
                       /** Prompt caching metrics（Anthropic 等支援的 provider） */
                       val cacheCreationTokens: Int? = null,
                       /** Prompt caching 命中的 input token 數（計費 10x 便宜） */
                       val cacheReadTokens: Int? = null) : Reply<T>()

  sealed class Error : Reply<Nothing>() {

    /**
     * orchestrator 可安全地對同一個 (provider, model) 重試。
     * 通常是 transient 狀態（server overload, rate limit）。
     *
     * 用 sealed class 而非 sealed interface，讓 Kotlin 單一繼承強制 [Retryable] / [Terminal] 互斥。
     */
    sealed class Retryable : Error()

    /**
     * orchestrator 不應對同一輸入再 retry 同一 model。
     * 改 input、換 model 或換 provider 才有救。
     */
    sealed class Terminal : Error()

    /** Input/context 太長 —— 改 input 才能解。 */
    data class TooLong(val message: String, override val provider: Provider) : Terminal()

    data class DeserializationFailure(val errorMessage: String,
                                      val originalContent: String,
                                      override val provider: Provider,
                                      val model: String) : Terminal()

    data class InvalidApiKey(override val provider: Provider) : Terminal()

    /** 籠統的 transient 錯誤（server overloaded / 未分類的 5xx 等）—— 可 retry。 */
    data class Busy(override val provider: Provider) : Retryable()

    /** 預設視為 Terminal —— 未知狀況保守處理，不重試。 */
    data class Unknown(val message: String, override val provider: Provider) : Terminal()

    /**
     * 明確的 rate limit（429 / quota per minute）。
     * @param retryAfter 若 provider 在 response header 給了 Retry-After 即帶入，否則 null。
     */
    data class RateLimited(
      override val provider: Provider,
      val retryAfter: Duration? = null,
      val message: String = ""
    ) : Retryable()

    /**
     * 輸出觸頂 max_tokens（finishReason = "length" / "max_tokens" / "MAX_TOKENS"）；
     * 內容部分產生但被截斷。同樣 input 重 retry 不會有救，要換 model 或加大 max_tokens。
     */
    data class MaxTokensReached(
      override val provider: Provider,
      val model: String,
      val partialContent: String? = null
    ) : Terminal()

    /**
     * Function-call loop 沒在 [maxDepth] 內收斂。
     * 通常是 model 卡在重複呼叫同一個 function、或 function 回的結果讓 model 無法決斷。
     */
    data class FunctionCallLoopExceeded(
      override val provider: Provider,
      val model: String,
      val maxDepth: Int
    ) : Terminal()
  }
}


interface IChatCompletion {

  val provider: Provider

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCalls: Set<IFunctionDeclaration> = emptySet(), timeout: Duration = 90.seconds, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec? = null, maxFunctionCallDepth: Int = DEFAULT_MAX_FUNCTION_CALL_DEPTH) : Reply<String>

  suspend fun chatComplete(model: String, messages: List<Msg>, user: String? = null, funCall: IFunctionDeclaration, timeout: Duration = 90.seconds, chatOptions: ChatOptions, maxFunctionCallDepth: Int = DEFAULT_MAX_FUNCTION_CALL_DEPTH) : Reply<String> {
    return chatComplete(model, messages, user, setOf(funCall), timeout, chatOptions, maxFunctionCallDepth = maxFunctionCallDepth)
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
    timeout: Duration = 90.seconds,
    maxFunctionCallDepth: Int = DEFAULT_MAX_FUNCTION_CALL_DEPTH
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
    timeout: Duration = 90.seconds,
    maxFunctionCallDepth: Int = DEFAULT_MAX_FUNCTION_CALL_DEPTH
  ): Reply<T>? {
    return typedChatComplete(model, listOf(Msg(Role.USER, message)), formatSpec, json, locale, chatOptions, postProcessors, user, funCalls, timeout, maxFunctionCallDepth)
  }

  companion object {
    /**
     * Function-call loop 預設最大遞迴深度。一輪 = 一次 model→function→model 的交換。
     * 超過會回傳 [Reply.Error.FunctionCallLoopExceeded]，避免 model 卡在重複呼叫 / stack overflow / 燒 token。
     */
    const val DEFAULT_MAX_FUNCTION_CALL_DEPTH = 10
  }
}

abstract class AbstractChatCompletion : IChatCompletion {

  abstract suspend fun doChatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec? = null, maxFunctionCallDepth: Int = IChatCompletion.DEFAULT_MAX_FUNCTION_CALL_DEPTH): Reply<String>

  override suspend fun chatComplete(model: String, messages: List<Msg>, user: String?, funCalls: Set<IFunctionDeclaration>, timeout: Duration, chatOptions: ChatOptions, jsonSchema: JsonSchemaSpec?, maxFunctionCallDepth: Int): Reply<String> {
    val filteredFunCalls = funCalls.filter { it.applied(messages) }.toSet()

    val finalMsgs = messages.fold(mutableListOf<Msg>()) { acc, msg ->
      if (acc.isNotEmpty()) {
        val lastMsg = acc.last()
        // 不合併 SYSTEM 訊息 —— 因為每則可能有不同的 [Msg.cacheable] 策略
        // （Anthropic prompt caching 要穩定的 block 單獨 cache、不穩定的另一 block 不 cache），
        // 合併會讓 cache_control boundary 錯位、cache key 失配。
        val sameRoleNonSystem = lastMsg.role == msg.role && msg.role != Role.SYSTEM
        val sameCacheable = lastMsg.cacheable == msg.cacheable
        if (sameRoleNonSystem && sameCacheable) {
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

    return doChatComplete(model, finalMsgs, user, filteredFunCalls, timeout, chatOptions, jsonSchema, maxFunctionCallDepth)
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
    timeout: Duration,
    maxFunctionCallDepth: Int
  ): Reply<T>? {

    return when (val rawReply: Reply<String> = chatComplete(model, messages, user, funCalls, timeout, chatOptions, formatSpec.jsonSchema, maxFunctionCallDepth)) {
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
          duration = rawReply.duration,
          cacheCreationTokens = rawReply.cacheCreationTokens,
          cacheReadTokens = rawReply.cacheReadTokens,
        )
      }

      is Reply.Error -> rawReply
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
