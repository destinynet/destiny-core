/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Segment Definitions
 */
package destiny.tools.workflow

import destiny.tools.ai.model.FormatSpec
import kotlin.reflect.KClass

/**
 * 內容區段
 *
 * 代表 GenerationPlan 的一個生成單元，可以是：
 * - AiSegment: 由 AI 生成的區段（支援兩種模式：FormatSpec 自動反序列化或手動 outputParser）
 * - ParallelAiSegment: 並行的 AI 生成區段
 * - ComputeSegment: 由程式計算的區段
 * - StaticSegment: 預先準備好的靜態內容
 */
sealed class Segment {
  /** 每個 segment 都有唯一識別碼 */
  abstract val id: SegmentId

  /** 依賴的 segment IDs */
  abstract val dependsOn: Set<SegmentId>

  /**
   * AI 生成區段 - 呼叫 AI API 生成內容
   *
   * 支援兩種模式：
   * 1. FormatSpec 模式（推薦）：提供 formatSpec，底層自動處理 JSON schema 和反序列化
   * 2. 手動解析模式：提供 outputParser 手動解析原始字串
   *
   * @property id 區段識別碼
   * @property dependsOn 依賴的 segment IDs
   * @property inputBuilder 建構輸入資料的函數
   * @property promptBuilder 建構 prompt 的函數
   * @property formatSpec FormatSpec 用於結構化輸出（推薦）- 包含 JSON schema 和自動反序列化
   * @property outputParser 手動解析 AI 輸出的函數（當 formatSpec 為 null 時使用）
   * @property expectedOutputType 預期的輸出類型（當使用 outputParser 時需要）
   */
  data class AiSegment<O : SegmentOutput>(
    override val id: SegmentId,
    override val dependsOn: Set<SegmentId> = emptySet(),
    val inputBuilder: (SegmentContext) -> SegmentInput,
    val promptBuilder: (input: SegmentInput, context: SegmentContext) -> String,
    val formatSpec: FormatSpec<O>? = null,
    val outputParser: ((rawOutput: String) -> O)? = null,
    val expectedOutputType: KClass<out SegmentOutput>? = null
  ) : Segment() {
    init {
      require(formatSpec != null || outputParser != null) {
        "Either formatSpec or outputParser must be provided for AiSegment $id"
      }
    }
  }

  /**
   * 並行 AI 區段 - 對集合中每個元素並行呼叫 AI
   *
   * 支援兩種模式：
   * 1. FormatSpec 模式（推薦）：提供 formatSpec，底層自動處理 JSON schema 和反序列化
   * 2. 手動解析模式：提供 outputParser 手動解析原始字串
   *
   * @property id 區段識別碼
   * @property dependsOn 依賴的 segment IDs
   * @property itemsProvider 提供要並行處理的項目列表
   * @property itemInputBuilder 為每個項目建構輸入資料
   * @property promptBuilder 建構 prompt 的函數
   * @property formatSpec FormatSpec 用於結構化輸出（推薦）- 包含 JSON schema 和自動反序列化
   * @property outputParser 手動解析 AI 輸出的函數（當 formatSpec 為 null 時使用）
   * @property expectedOutputType 預期的輸出類型（當使用 outputParser 時需要）
   * @property resultValidator 可選的結果驗證器，用於決定是否允許部分失敗
   *   - null (預設): 允許部分失敗，只要有結果即可
   *   - 提供驗證器: 根據驗證結果決定是否接受
   */
  data class ParallelAiSegment<T, O : SegmentOutput>(
    override val id: SegmentId,
    override val dependsOn: Set<SegmentId> = emptySet(),
    val itemsProvider: (SegmentContext) -> List<T>,
    val itemInputBuilder: (item: T, context: SegmentContext) -> SegmentInput,
    val promptBuilder: (input: SegmentInput, context: SegmentContext) -> String,
    val formatSpec: FormatSpec<O>? = null,
    val outputParser: ((rawOutput: String) -> O)? = null,
    val expectedOutputType: KClass<out SegmentOutput>? = null,
    val resultValidator: ParallelResultValidator? = null
  ) : Segment() {
    init {
      require(formatSpec != null || outputParser != null) {
        "Either formatSpec or outputParser must be provided for ParallelAiSegment $id"
      }
    }
  }

  /**
   * 計算區段 - 程式邏輯計算
   *
   * @property id 區段識別碼
   * @property dependsOn 依賴的 segment IDs
   * @property compute 計算函數，從 context 取得依賴結果並產生輸出
   */
  data class ComputeSegment(
    override val id: SegmentId,
    override val dependsOn: Set<SegmentId> = emptySet(),
    val compute: (SegmentContext) -> SegmentOutput
  ) : Segment()

  /**
   * 靜態區段 - 預先準備好的內容
   *
   * @property id 區段識別碼
   * @property dependsOn 依賴的 segment IDs（通常為空）
   * @property content 預先準備好的內容
   */
  data class StaticSegment(
    override val id: SegmentId,
    override val dependsOn: Set<SegmentId> = emptySet(),
    val content: SegmentOutput
  ) : Segment()
}
