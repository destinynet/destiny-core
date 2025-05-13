/**
 * Created by smallufo on 2025-05-13.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.ResilientChatService.ResilientConfig
import destiny.tools.suspendFirstNotNullResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration
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
 * 直到達到配置的最大嘗試次數 ([ResilientConfig.maxTotalAttempts])。
 *
 * 與 [HedgeChatService] 不同（後者通過並行請求優先考慮低延遲），
 * 此服務優先考慮**最終的成功率**，即使可能需要更長的處理時間。
 *
 * @property domainModelService 用於根據提供者查找具體聊天完成實現的服務。
 * @property config 控制重試行為（例如，總嘗試次數、循環間延遲）的配置。
 */
class ResilientChatService(
  private val domainModelService: IDomainModelService,
  val config: ResilientConfig
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
    val delayBetweenModelLoops: Duration = 2.seconds, // 在完整輪詢所有模型都失敗後，再次開始新一輪輪詢前的延遲
    val maxTotalAttempts: Int = 3 // 最多進行多少輪完整的模型輪詢
  )

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
  ): ResultDto<T>? {
    val providerModels = config.providerModels

    if (providerModels.isEmpty()) {
      logger.warn { "No provider models specified for resilient chat completion." }
      return null
    }

    val shuffledModels: List<ProviderModel> = providerModels.shuffled()


    var attemptsLeft = config.maxTotalAttempts
    while (attemptsLeft > 0) {

      logger.info { "Starting a new attempt loop (attempts left: $attemptsLeft). Shuffled models: ${shuffledModels.map { it.model }}" }
      val successfulResultDto = process(serializer, shuffledModels, messages, user, funCalls, jsonSchema, chatOptionsTemplate, modelTimeout, postProcessors, locale)

      if (successfulResultDto != null) {
        logger.info { "Successfully obtained result from ${successfulResultDto.provider}/${successfulResultDto.model} within the loop." }
        return successfulResultDto // 找到結果，整個 resilientChatComplete 函數返回
      }

      // 如果一輪循環後都沒有成功
      attemptsLeft--
      if (attemptsLeft > 0) {
        logger.info { "All models in the current loop failed. Retrying after ${config.delayBetweenModelLoops} delay. Attempts left: $attemptsLeft" }
        delay(config.delayBetweenModelLoops)
      }
    } // end of while loop (attemptsLeft)

    logger.error { "All attempts to get a resilient chat completion failed after ${config.maxTotalAttempts} loops." }
    return null
  }

  /**
   * 處理一輪（打亂後的）模型列表的嘗試。
   * 它使用 suspendFirstNotNullResult 依次嘗試每個模型，直到找到第一個成功的結果。
   * （假設此函數是 private inline fun <reified T>）
   */
  suspend fun <T> process(
    serializer: KSerializer<T>,
    shuffledModels: List<ProviderModel>,
    messages: List<Msg>,
    user: String? = null,
    funCalls: Set<IFunctionDeclaration> = emptySet(),
    jsonSchema: JsonSchemaSpec? = null,
    chatOptionsTemplate: ChatOptions, // 提供一個聊天選項模板
    modelTimeout: Duration,
    postProcessors: List<IPostProcessor>,
    locale: Locale
  ): ResultDto<T>? {
    return shuffledModels.suspendFirstNotNullResult { providerModel ->
      logger.debug { "Attempting model (suspend loop): ${providerModel.provider}/${providerModel.model}" }
      try {
        val impl: IChatCompletion = domainModelService.findImpl(providerModel.provider)
        val currentChatOptions = chatOptionsTemplate.copy(
          temperature = providerModel.temperature ?: chatOptionsTemplate.temperature
        )

        val reply = withContext(Dispatchers.IO) {
          impl.chatComplete(
            model = providerModel.model,
            messages = messages,
            user = user,
            funCalls = funCalls,
            timeout = modelTimeout,
            chatOptions = currentChatOptions,
            jsonSchema = jsonSchema
          )
        }

        when (reply) {
          is Reply.Normal -> {
            logger.debug { "Model ${providerModel.model} returned Normal reply. Content length: ${reply.content.length}" }

            val processedContent = postProcessors.fold(reply.content) { currentContent, postProcessor ->
              val (nextContent, _) = postProcessor.process(currentContent, locale)
              nextContent
            }
            logger.debug { "Final post-processed content for ${providerModel.model}: $processedContent" }

            val result: T = try {
              json.decodeFromString(serializer, processedContent)
            } catch (e: SerializationException) {
              logger.warn(e) { "Failed to deserialize content from ${providerModel.model}. Content: $processedContent" }
              return@suspendFirstNotNullResult null
            }

            // 成功，返回 ResultDto 給 firstNotNullResult
            ResultDto(
              result = result,
              provider = providerModel.provider,
              model = providerModel.model,
              inputTokens = reply.inputTokens,
              outputTokens = reply.outputTokens
            )
          }

          is Reply.Error  -> {
            logger.warn { "Model ${providerModel.model} returned error: $reply" }
            null // 此模型嘗試失敗
          }
        }
      } catch (e: Exception) {
        logger.error(e) { "Exception during chat completion with ${providerModel.provider}/${providerModel.model}" }
        null // 發生異常，此模型嘗試失敗
      }
    } // end of suspendFirstNotNullResult's transform lambda
  }


  companion object {
    val logger = KotlinLogging.logger {}
  }
}
