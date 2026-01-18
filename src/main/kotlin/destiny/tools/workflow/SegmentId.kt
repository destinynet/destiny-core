/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Segment Identification
 */
package destiny.tools.workflow

import kotlinx.serialization.Serializable

/**
 * Segment 識別碼 - 用於依賴引用
 *
 * 使用 value class 包裝 String，提供：
 * - 類型安全（不會與其他 String 混淆）
 * - 零運行時開銷
 * - 可序列化
 */
@JvmInline
@Serializable
value class SegmentId(val value: String) {
  override fun toString(): String = value
}
