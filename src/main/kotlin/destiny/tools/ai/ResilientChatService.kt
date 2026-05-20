/**
 * Created by smallufo on 2025-05-13.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.model.FormatSpec
import destiny.tools.suspendFirstNotNullResult
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds


/**
 * 提供具有彈性的聊天完成功能。
 *
 * 該服務通過順序嘗試一組指定的語言模型 ([ProviderModel]) 來工作。
 * 在每次嘗試循環中，它會隨機打亂模型列表，然後依次調用每個模型，
 * 直到其中一個模型成功返回響應、完成所有後處理步驟，並且（如果需要）
 * 成功將結果反序列化為目標類型 [T]。
 *
 * 如果一輪循環中的所有模型都失敗了，服務將在延遲一段時間後開始新一輪的嘗試，
 * 直到達到配置的最大嘗試次數 ([maxTotalAttempts])。
 *
 * **基於 [Reply.Error] 的 marker interface 進行錯誤分類**：
 * - [Reply.Error.InvalidApiKey] —— 永久排除該 provider，不再於後續 loop 嘗試
 * - [Reply.Error.RateLimited] —— 收集 `retryAfter` hint；下一輪 loop 至少等到
 *   max(delayBetweenModelLoops, 本 loop 看到的最長 retryAfter) 才開始
 * - 其他 [Reply.Error] —— 紀錄並移到下一個 model（這個 model 下輪可再試）
 *
 * 與 [HedgeChatService] 不同（後者通過並行請求優先考慮低延遲），
 * 此服務優先考慮**最終的成功率**，即使可能需要更長的處理時間。
 *
 * @property config 控制重試行為（例如，總嘗試次數、循環間延遲）的配置。
 */
class ResilientChatService(
  private val config: ResilientConfig
) : IChatOrchestrator {

  @OptIn(ExperimentalSerializationApi::class)
  val json: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    allowTrailingComma = true
    isLenient = true
  }

  // 可以定義一個配置類
  data class ResilientConfig(
    val providerModels: Set<ProviderModel>,
    override val modelTimeout: Duration, // 在完整輪詢所有模型都失敗後，再次開始新一輪輪詢前的延遲
    val delayBetweenModelLoops: Duration = 2.seconds, // 最多進行多少輪完整的模型輪詢
    val maxTotalAttempts: Int = 3,
    override val user: String? = null,
  ) : IChatConfig

  @Suppress("UNCHECKED_CAST")
  override suspend fun <T : Any> chatComplete(
    formatSpec: FormatSpec<out T>,
    messages: List<Msg>,
    postProcessors: List<IPostProcessor>,
    locale: Locale,
    funCalls: Set<IFunctionDeclaration>,
    chatOptionsTemplate: ChatOptions,
    providerImpl: (Provider) -> IChatCompletion
  ): Reply.Normal<T>? {
    val providerModels = config.providerModels

    if (providerModels.isEmpty()) {
      logger.warn { "No provider models specified for resilient chat completion." }
      return null
    }

    // 跨 loop 共享的狀態：API key 無效的 provider 整個 session 都不再嘗試
    val disabledProviders = mutableSetOf<Provider>()

    var attemptsLeft = config.maxTotalAttempts
    while (attemptsLeft > 0) {
      val candidates: List<ProviderModel> = providerModels
        .filter { it.provider !in disabledProviders }
        .shuffled()

      if (candidates.isEmpty()) {
        logger.error { "All providers disabled (invalid API keys or similar); aborting after ${config.maxTotalAttempts - attemptsLeft} loops." }
        return null
      }

      logger.info { "Starting attempt loop (attemptsLeft=$attemptsLeft), candidates: ${candidates.map { "${it.provider}/${it.model}" }}" }

      // 收集本 loop 內看到的 RateLimited.retryAfter，用來計算下一 loop 的最小等待時間
      var maxRetryAfter: Duration = ZERO

      val result: Reply.Normal<T>? = candidates.suspendFirstNotNullResult { providerModel ->
        logger.debug { "Attempting ${providerModel.provider}/${providerModel.model}" }
        try {
          val impl = providerImpl.invoke(providerModel.provider)
          val currentChatOptions = chatOptionsTemplate.copy(
            temperature = providerModel.temperature ?: chatOptionsTemplate.temperature
          )

          val reply = impl.typedChatComplete(
            providerModel.model, messages, formatSpec, json, locale,
            currentChatOptions, postProcessors, config.user, funCalls, config.modelTimeout
          )

          when (reply) {
            is Reply.Normal<*> -> reply as Reply.Normal<T>

            is Reply.Error.InvalidApiKey -> {
              // session-permanent：剩下的 loop 都不要再試這個 provider
              logger.warn { "InvalidApiKey on ${reply.provider}; disabling provider for the rest of this call" }
              disabledProviders += reply.provider
              null
            }

            is Reply.Error.RateLimited -> {
              reply.retryAfter?.let { hint ->
                if (hint > maxRetryAfter) maxRetryAfter = hint
              }
              logger.warn { "Retryable[RateLimited] on ${providerModel.provider}/${providerModel.model}, retryAfter=${reply.retryAfter}, msg=${reply.message}" }
              null
            }

            is Reply.Error.Retryable -> {
              logger.warn { "Retryable on ${providerModel.provider}/${providerModel.model}: $reply" }
              null
            }

            is Reply.Error.Terminal -> {
              logger.warn { "Terminal on ${providerModel.provider}/${providerModel.model}: $reply" }
              null
            }

            is Reply.Error -> {
              // 安全網：未來新增的 leaf 沒實作 Retryable/Terminal marker 時也能 fall through
              logger.warn { "Unclassified error on ${providerModel.provider}/${providerModel.model}: $reply" }
              null
            }

            null -> {
              logger.warn { "typedChatComplete returned null for ${providerModel.provider}/${providerModel.model}" }
              null
            }
          }
        } catch (e: Exception) {
          logger.error(e) { "Exception during chat completion with ${providerModel.provider}/${providerModel.model}" }
          null
        }
      }

      if (result != null) {
        logger.info { "Success on ${result.provider}/${result.model} (attemptsLeft=$attemptsLeft)" }
        return result
      }

      attemptsLeft--
      if (attemptsLeft > 0) {
        // 若本 loop 有 model 給了 retryAfter hint，至少等到那麼久後再開新一輪
        val backoff = maxOf(config.delayBetweenModelLoops, maxRetryAfter)
        logger.info { "All candidates failed this loop. Sleeping $backoff before next loop (attemptsLeft=$attemptsLeft, maxRetryAfter=$maxRetryAfter)" }
        delay(backoff)
      }
    }

    logger.error { "All ${config.maxTotalAttempts} attempt loops exhausted." }
    return null
  }


  companion object {
    val logger = KotlinLogging.logger {}
  }
}
