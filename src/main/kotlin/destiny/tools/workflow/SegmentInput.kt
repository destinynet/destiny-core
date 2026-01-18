/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Segment Input Types
 */
package destiny.tools.workflow

/**
 * Segment 輸入基礎介面
 *
 * 所有 segment 的輸入都實作此介面。
 * 由 inputBuilder 或 itemInputBuilder 產生，傳入 promptBuilder。
 */
interface SegmentInput

/**
 * 空輸入 - 用於不需要額外輸入的 segment
 */
data object EmptyInput : SegmentInput

/**
 * 原始字串輸入
 */
data class RawInput(val data: String) : SegmentInput
