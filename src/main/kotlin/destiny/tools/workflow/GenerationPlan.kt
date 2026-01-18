/**
 * Created by smallufo on 2026-01-18.
 *
 * Unified Workflow Engine - Generation Plan
 */
package destiny.tools.workflow

/**
 * 統一的生成計畫
 *
 * 定義完整的執行流程，包含：
 * - 所有 segments 及其依賴關係
 * - 最終輸出組裝邏輯
 *
 * @param R 最終結果類型
 * @property planId 計畫識別碼
 * @property name 計畫名稱
 * @property segments 所有 segments
 * @property assembler 最終結果組裝函數
 */
data class GenerationPlan<R>(
  val planId: String,
  val name: String,
  val segments: List<Segment>,
  val assembler: (SegmentContext) -> R
) {
  init {
    // 驗證：segment IDs 必須唯一
    val segmentIds = segments.map { it.id }
    val duplicates = segmentIds.groupingBy { it }.eachCount().filter { it.value > 1 }
    require(duplicates.isEmpty()) {
      "Duplicate segment IDs found: ${duplicates.keys.joinToString { it.value }}"
    }

    // 驗證：所有依賴都必須存在
    val segmentIdSet = segmentIds.toSet()
    segments.forEach { segment ->
      segment.dependsOn.forEach { dep ->
        require(dep in segmentIdSet) {
          "Segment ${segment.id} depends on unknown segment $dep"
        }
      }
    }

    // 驗證：無循環依賴
    validateNoCycles()
  }

  /**
   * 驗證計畫中無循環依賴
   */
  private fun validateNoCycles() {
    val visited = mutableSetOf<SegmentId>()
    val recursionStack = mutableSetOf<SegmentId>()

    fun hasCycle(segmentId: SegmentId): Boolean {
      if (segmentId in recursionStack) return true
      if (segmentId in visited) return false

      visited.add(segmentId)
      recursionStack.add(segmentId)

      val segment = segments.find { it.id == segmentId }
      segment?.dependsOn?.forEach { dep ->
        if (hasCycle(dep)) return true
      }

      recursionStack.remove(segmentId)
      return false
    }

    segments.forEach { segment ->
      require(!hasCycle(segment.id)) {
        "Circular dependency detected involving segment ${segment.id}"
      }
    }
  }

  /**
   * 取得 segment 的拓撲排序
   * @return 按依賴順序排列的 segment 列表（被依賴的在前）
   */
  fun topologicalSort(): List<Segment> {
    val result = mutableListOf<Segment>()
    val visited = mutableSetOf<SegmentId>()
    val segmentMap = segments.associateBy { it.id }

    fun visit(segment: Segment) {
      if (segment.id in visited) return
      segment.dependsOn.forEach { depId ->
        segmentMap[depId]?.let { visit(it) }
      }
      visited.add(segment.id)
      result.add(segment)
    }

    segments.forEach { visit(it) }
    return result
  }

  /**
   * 取得可以並行執行的 segment 層級
   * @return 層級列表，每層的 segments 可以並行執行
   */
  fun executionLayers(): List<List<Segment>> {
    val layers = mutableListOf<List<Segment>>()
    val completed = mutableSetOf<SegmentId>()
    val remaining = segments.toMutableList()

    while (remaining.isNotEmpty()) {
      // 找出所有依賴都已完成的 segments
      val ready = remaining.filter { segment ->
        segment.dependsOn.all { it in completed }
      }

      if (ready.isEmpty() && remaining.isNotEmpty()) {
        throw IllegalStateException("Unable to resolve dependencies - possible cycle")
      }

      layers.add(ready)
      ready.forEach { completed.add(it.id) }
      remaining.removeAll(ready)
    }

    return layers
  }
}
