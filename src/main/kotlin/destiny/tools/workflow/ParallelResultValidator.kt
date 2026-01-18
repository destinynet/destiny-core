/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Parallel Result Validation
 */
package destiny.tools.workflow

/**
 * 並行結果驗證器
 *
 * 用於 ParallelAiSegment，決定是否接受部分失敗的結果。
 */
fun interface ParallelResultValidator {
  /**
   * 驗證並行執行的結果
   *
   * @param expectedCount 預期的項目數量 (itemsProvider 返回的 list size)
   * @param successCount 成功的項目數量
   * @param results 成功的結果列表
   * @return ValidationResult - 接受或拒絕（包含原因）
   */
  fun validate(expectedCount: Int, successCount: Int, results: List<SegmentOutput>): ValidationResult
}

/**
 * 驗證結果
 */
sealed class ValidationResult {
  /** 驗證通過，接受結果 */
  data object Accepted : ValidationResult()

  /** 驗證失敗，拒絕結果 */
  data class Rejected(val reason: String) : ValidationResult()

  fun isAccepted(): Boolean = this is Accepted
  fun isRejected(): Boolean = this is Rejected
}

/**
 * 內建的驗證器工廠
 */
object Validators {

  /** 必須全部成功 */
  val requireAll = ParallelResultValidator { expected, success, _ ->
    if (success == expected) ValidationResult.Accepted
    else ValidationResult.Rejected("Expected $expected items but only $success succeeded")
  }

  /** 要求最少成功數量 */
  fun requireAtLeast(minCount: Int) = ParallelResultValidator { _, success, _ ->
    if (success >= minCount) ValidationResult.Accepted
    else ValidationResult.Rejected("Required at least $minCount items but only $success succeeded")
  }

  /** 要求最低成功率 */
  fun requireSuccessRate(minRate: Double) = ParallelResultValidator { expected, success, _ ->
    val rate = if (expected == 0) 1.0 else success.toDouble() / expected
    if (rate >= minRate) ValidationResult.Accepted
    else ValidationResult.Rejected(
      "Success rate ${(rate * 100).toInt()}% below required ${(minRate * 100).toInt()}%"
    )
  }

  /** 自定義驗證邏輯 */
  fun custom(
    validator: (expectedCount: Int, successCount: Int, results: List<SegmentOutput>) -> ValidationResult
  ) = ParallelResultValidator(validator)
}
