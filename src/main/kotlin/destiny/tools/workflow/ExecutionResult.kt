/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Execution Results
 */
package destiny.tools.workflow

/**
 * 執行結果
 */
sealed class ExecutionResult<R> {
  /**
   * 執行成功
   */
  data class Success<R>(
    val result: R,
    val metadata: ExecutionMetadata
  ) : ExecutionResult<R>()

  /**
   * 執行失敗
   */
  data class Failed<R>(
    val failedSegment: SegmentId,
    val error: Throwable,
    val partialResults: Map<SegmentId, SegmentOutput>,
    val metadata: ExecutionMetadata
  ) : ExecutionResult<R>()

  fun isSuccess(): Boolean = this is Success
  fun isFailed(): Boolean = this is Failed

  fun getOrNull(): R? = (this as? Success)?.result

  fun getOrThrow(): R = when (this) {
    is Success -> result
    is Failed -> throw ExecutionException(
      "Execution failed at segment $failedSegment",
      error
    )
  }
}

/**
 * 執行例外
 */
class ExecutionException(
  message: String,
  cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 執行元資料
 */
data class ExecutionMetadata(
  /** 總執行時間 (毫秒) */
  val totalDurationMs: Long,

  /** 每個 segment 的執行時間 */
  val segmentDurations: Map<SegmentId, Long>,

  /** 並行執行次數 */
  val parallelExecutions: Int,

  /** AI 呼叫總次數 */
  val totalAiCalls: Int,

  /** Token 使用量 */
  val tokenUsage: TokenUsage? = null
) {
  companion object {
    fun empty() = ExecutionMetadata(
      totalDurationMs = 0,
      segmentDurations = emptyMap(),
      parallelExecutions = 0,
      totalAiCalls = 0
    )
  }
}

/**
 * Token 使用量
 */
data class TokenUsage(
  val inputTokens: Int,
  val outputTokens: Int
) {
  val totalTokens: Int get() = inputTokens + outputTokens

  operator fun plus(other: TokenUsage) = TokenUsage(
    inputTokens = this.inputTokens + other.inputTokens,
    outputTokens = this.outputTokens + other.outputTokens
  )

  companion object {
    val ZERO = TokenUsage(0, 0)
  }
}
