/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Segment Context
 */
package destiny.tools.workflow

import kotlin.reflect.KClass

/**
 * 提供對已執行 segment 結果的結構化訪問
 */
interface SegmentContext {
  /**
   * 內部實作 - 需要 KClass 參數
   * @throws NoSuchElementException 如果 segment 不存在或尚未執行
   * @throws ClassCastException 如果類型不匹配
   */
  fun <T : SegmentOutput> getInternal(id: SegmentId, type: KClass<T>): T

  /**
   * 內部實作 - 可能為 null
   */
  fun <T : SegmentOutput> getOrNullInternal(id: SegmentId, type: KClass<T>): T?

  /**
   * 檢查 segment 是否已執行完成
   */
  fun isCompleted(id: SegmentId): Boolean

  /**
   * 取得所有已完成的 segment IDs
   */
  fun completedSegments(): Set<SegmentId>
}

/**
 * 使用 reified inline function 提供簡潔的 API
 * - 自動推斷類型，無需傳入 KClass
 * - inline 確保零運行時開銷
 */
inline fun <reified T : SegmentOutput> SegmentContext.get(id: SegmentId): T =
  getInternal(id, T::class)

inline fun <reified T : SegmentOutput> SegmentContext.getOrNull(id: SegmentId): T? =
  getOrNullInternal(id, T::class)

/**
 * SegmentContext 的可變實作
 */
class MutableSegmentContext : SegmentContext {
  private val results = mutableMapOf<SegmentId, SegmentOutput>()

  override fun <T : SegmentOutput> getInternal(id: SegmentId, type: KClass<T>): T {
    val result = results[id] ?: throw NoSuchElementException("Segment $id not found or not yet executed")
    @Suppress("UNCHECKED_CAST")
    return result as? T ?: throw ClassCastException("Segment $id output is not of type ${type.simpleName}")
  }

  override fun <T : SegmentOutput> getOrNullInternal(id: SegmentId, type: KClass<T>): T? {
    val result = results[id] ?: return null
    @Suppress("UNCHECKED_CAST")
    return result as? T
  }

  override fun isCompleted(id: SegmentId): Boolean = id in results

  override fun completedSegments(): Set<SegmentId> = results.keys.toSet()

  /**
   * 設定 segment 的執行結果
   */
  fun put(id: SegmentId, output: SegmentOutput) {
    results[id] = output
  }

  /**
   * 取得所有結果的不可變副本
   */
  fun toMap(): Map<SegmentId, SegmentOutput> = results.toMap()
}
