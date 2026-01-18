/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Execution Engine
 */
package destiny.tools.workflow

import destiny.tools.KotlinLogging
import destiny.tools.ai.*
import kotlinx.coroutines.*
import kotlinx.coroutines.supervisorScope
import java.util.*
import kotlin.time.measureTimedValue

/**
 * 執行引擎介面
 */
interface ExecutionEngine {
  /**
   * 執行生成計畫
   *
   * @param plan 要執行的計畫
   * @param locale 語言環境
   * @param initialContext 初始上下文 (可選)
   * @return 執行結果
   */
  suspend fun <R> execute(
    plan: GenerationPlan<R>,
    locale: Locale = Locale.getDefault(),
    initialContext: Map<SegmentId, SegmentOutput> = emptyMap()
  ): ExecutionResult<R>
}

/**
 * 執行進度監聽器
 */
interface ExecutionProgressListener {
  fun onSegmentStarted(id: SegmentId)
  fun onSegmentCompleted(id: SegmentId, durationMs: Long)
  fun onSegmentFailed(id: SegmentId, error: Throwable, willRetry: Boolean)
  fun onSegmentRetrying(id: SegmentId, attempt: Int, maxAttempts: Int)
}

/**
 * 預設執行引擎實作
 *
 * 直接使用 IChatOrchestrator 進行 AI 呼叫，整合現有的：
 * - JSON Schema 指導 AI 輸出格式
 * - PostProcessor 處理（JSON 提取、中文處理等）
 * - 自動反序列化為目標類型
 *
 * @param orchestrator AI 聊天編排器
 * @param postProcessors 後處理器列表（JSON 提取、中文處理等）
 * @param providerImpl 提供者實作函數，用於取得特定 Provider 的 IChatCompletion
 * @param maxRetries 最大重試次數
 * @param retryDelayMs 重試延遲（毫秒）
 * @param progressListener 執行進度監聽器（可選）
 */
class DefaultExecutionEngine(
  private val orchestrator: IChatOrchestrator,
  private val postProcessors: List<IPostProcessor>,
  private val providerImpl: (Provider) -> IChatCompletion,
  private val maxRetries: Int = 3,
  private val retryDelayMs: Long = 1000,
  private val progressListener: ExecutionProgressListener? = null
) : ExecutionEngine {

  private val logger = KotlinLogging.logger { }

  override suspend fun <R> execute(
    plan: GenerationPlan<R>,
    locale: Locale,
    initialContext: Map<SegmentId, SegmentOutput>
  ): ExecutionResult<R> = supervisorScope {
    val startTime = System.currentTimeMillis()
    val context = MutableSegmentContext()
    val segmentDurations = mutableMapOf<SegmentId, Long>()
    var parallelExecutions = 0
    var totalAiCalls = 0

    // 初始化 context
    initialContext.forEach { (id, output) ->
      context.put(id, output)
    }

    try {
      // 按層級執行
      val layers = plan.executionLayers()

      for (layer in layers) {
        if (layer.size > 1) {
          parallelExecutions++
        }

        // 並行執行同一層的 segments
        val results = layer.map { segment ->
          async {
            val (result, duration) = measureTimedValue {
              executeSegment(segment, context, locale)
            }
            segmentDurations[segment.id] = duration.inWholeMilliseconds

            // 統計 AI 呼叫次數
            when (segment) {
              is Segment.AiSegment<*> -> totalAiCalls++
              is Segment.ParallelAiSegment<*, *> -> {
                val items = segment.itemsProvider(context)
                totalAiCalls += items.size
              }
              else -> {}
            }

            segment.id to result
          }
        }.awaitAll()

        // 將結果放入 context
        results.forEach { (id, output) ->
          context.put(id, output)
        }
      }

      // 組裝最終結果
      val finalResult = plan.assembler(context)

      val metadata = ExecutionMetadata(
        totalDurationMs = System.currentTimeMillis() - startTime,
        segmentDurations = segmentDurations.toMap(),
        parallelExecutions = parallelExecutions,
        totalAiCalls = totalAiCalls
      )

      ExecutionResult.Success(finalResult, metadata)

    } catch (e: Exception) {
      logger.error(e) { "Execution failed" }

      val metadata = ExecutionMetadata(
        totalDurationMs = System.currentTimeMillis() - startTime,
        segmentDurations = segmentDurations.toMap(),
        parallelExecutions = parallelExecutions,
        totalAiCalls = totalAiCalls
      )

      // 嘗試找出失敗的 segment
      val failedSegmentId = when (e) {
        is SegmentExecutionException -> e.segmentId
        else -> SegmentId("unknown")
      }

      ExecutionResult.Failed(
        failedSegment = failedSegmentId,
        error = e,
        partialResults = context.toMap(),
        metadata = metadata
      )
    }
  }

  private suspend fun executeSegment(
    segment: Segment,
    context: SegmentContext,
    locale: Locale
  ): SegmentOutput {
    progressListener?.onSegmentStarted(segment.id)

    return try {
      when (segment) {
        is Segment.AiSegment<*> -> executeAiSegment(segment, context, locale)
        is Segment.ParallelAiSegment<*, *> -> executeParallelAiSegment(segment, context, locale)
        is Segment.ComputeSegment -> executeComputeSegment(segment, context)
        is Segment.StaticSegment -> segment.content
      }.also { result ->
        progressListener?.onSegmentCompleted(segment.id, 0)
        logger.debug { "Segment ${segment.id} completed" }
      }
    } catch (e: Exception) {
      progressListener?.onSegmentFailed(segment.id, e, false)
      throw SegmentExecutionException(segment.id, e)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun <O : SegmentOutput> executeAiSegment(
    segment: Segment.AiSegment<O>,
    context: SegmentContext,
    locale: Locale
  ): SegmentOutput {
    val input = segment.inputBuilder(context)
    val prompt = segment.promptBuilder(input, context)

    val formatSpec = segment.formatSpec
      ?: throw IllegalStateException("Segment ${segment.id} must have formatSpec for DefaultExecutionEngine")

    var lastError: Exception? = null
    repeat(maxRetries) { attempt ->
      try {
        progressListener?.onSegmentRetrying(segment.id, attempt + 1, maxRetries)

        val reply = orchestrator.chatComplete(
          formatSpec = formatSpec,
          message = prompt,
          postProcessors = postProcessors,
          locale = locale,
          funCalls = emptySet(),
          chatOptionsTemplate = ChatOptions(),
          providerImpl = providerImpl
        ) ?: throw IllegalStateException("Orchestrator returned null for segment ${segment.id}")

        return reply.content as SegmentOutput

      } catch (e: Exception) {
        lastError = e
        logger.warn { "Segment ${segment.id} attempt ${attempt + 1} failed: ${e.message}" }
        if (attempt < maxRetries - 1) {
          delay(retryDelayMs * (attempt + 1))
        }
      }
    }

    throw lastError ?: IllegalStateException("Unexpected state in executeAiSegment")
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun <T, O : SegmentOutput> executeParallelAiSegment(
    segment: Segment.ParallelAiSegment<T, O>,
    context: SegmentContext,
    locale: Locale
  ): SegmentOutput = coroutineScope {
    val items = segment.itemsProvider(context)
    val expectedCount = items.size

    val formatSpec = segment.formatSpec
      ?: throw IllegalStateException("ParallelAiSegment ${segment.id} must have formatSpec for DefaultExecutionEngine")

    val results = items.mapIndexed { index, item ->
      async {
        var lastError: Throwable? = null
        repeat(maxRetries) { attempt ->
          try {
            val input = segment.itemInputBuilder(item, context)
            val prompt = segment.promptBuilder(input, context)

            val reply = orchestrator.chatComplete(
              formatSpec = formatSpec,
              message = prompt,
              postProcessors = postProcessors,
              locale = locale,
              funCalls = emptySet(),
              chatOptionsTemplate = ChatOptions(),
              providerImpl = providerImpl
            ) ?: throw IllegalStateException("Orchestrator returned null for parallel item $index")

            return@async Result.success(reply.content as SegmentOutput)

          } catch (e: Exception) {
            lastError = e
            if (attempt < maxRetries - 1) {
              delay(retryDelayMs * (attempt + 1))
            }
          }
        }
        Result.failure<SegmentOutput>(lastError!!)
      }
    }.awaitAll()

    val successful = results.filter { it.isSuccess }.map { it.getOrThrow() }
    val failed = results.mapIndexedNotNull { index, result ->
      if (result.isFailure) {
        val cause = result.exceptionOrNull()
        FailedItem(index, cause ?: Exception("Unknown error"), maxRetries)
      } else null
    }

    // 驗證結果
    val validator = segment.resultValidator
    if (validator != null) {
      val validationResult = validator.validate(expectedCount, successful.size, successful)
      if (validationResult is ValidationResult.Rejected) {
        throw ValidationException(segment.id, validationResult.reason)
      }
    }

    ParallelOutput(successful as List<Nothing>, failed)
  }

  private fun executeComputeSegment(
    segment: Segment.ComputeSegment,
    context: SegmentContext
  ): SegmentOutput {
    return segment.compute(context)
  }
}

/**
 * Segment 執行例外
 */
class SegmentExecutionException(
  val segmentId: SegmentId,
  cause: Throwable
) : RuntimeException("Segment $segmentId execution failed", cause)

/**
 * 驗證例外
 */
class ValidationException(
  val segmentId: SegmentId,
  val reason: String
) : RuntimeException("Segment $segmentId validation failed: $reason")
