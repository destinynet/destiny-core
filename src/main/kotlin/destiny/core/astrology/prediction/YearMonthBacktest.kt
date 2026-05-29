/**
 * YearMonth Search — 回測度量(純函式)。
 *
 * 給定一次搜尋產出的 [YearMonthWindow] 清單(已依 strength 降冪)與一個「已知大事發生的月份」,
 * 計算該月份在結果中的排名/百分位 —— 作為日後用 [YearMonthScoringConfig] 校準權重時的**目標函數**。
 *
 * 本檔不含任何資料集;呼叫端提供真實事件月份(避免捏造占星資料)。
 */
package destiny.core.astrology.prediction

import java.time.YearMonth

/** 單一已知事件的回測結果。 */
data class BacktestResult(
  val actualMonth: YearMonth,
  /** 是否有 window 覆蓋 actualMonth。 */
  val matched: Boolean,
  /** 覆蓋該月的 window 在排序中的名次(1-based);未命中為 null。 */
  val rank: Int?,
  val totalWindows: Int,
  /** `(total - rank + 1) / total`,1.0 = 排第一;未命中為 null。 */
  val percentile: Double?,
  val inTopN: Boolean,
  val topN: Int,
)

/** 多事件回測的彙總(校準時看這個)。 */
data class BacktestSummary(
  val results: List<BacktestResult>,
  /** 有 window 覆蓋的事件數。 */
  val matchedCount: Int,
  /** inTopN 命中率(分母 = 全部事件)。 */
  val topNHitRate: Double,
  /** 命中事件的平均百分位(無命中為 0)。 */
  val meanPercentile: Double,
)

object YearMonthBacktest {

  /**
   * @param windows 一次搜尋的結果,**假設已依 strength 降冪**(`YearMonthSearchService.search` 即如此)。
   * @param actualMonth 已知大事發生的月份。
   * @param topN 視為「命中」的名次門檻。
   */
  fun evaluate(windows: List<YearMonthWindow>, actualMonth: YearMonth, topN: Int = 3): BacktestResult {
    val total = windows.size
    val idx = windows.indexOfFirst { actualMonth in it.from..it.to }
    if (idx < 0) {
      return BacktestResult(actualMonth, matched = false, rank = null, totalWindows = total, percentile = null, inTopN = false, topN = topN)
    }
    val rank = idx + 1
    return BacktestResult(
      actualMonth = actualMonth,
      matched = true,
      rank = rank,
      totalWindows = total,
      percentile = (total - rank + 1).toDouble() / total,
      inTopN = rank <= topN,
      topN = topN,
    )
  }

  fun summarize(results: List<BacktestResult>): BacktestSummary {
    val matched = results.filter { it.matched }
    val topNHitRate = if (results.isEmpty()) 0.0 else results.count { it.inTopN }.toDouble() / results.size
    val meanPercentile = if (matched.isEmpty()) 0.0 else matched.mapNotNull { it.percentile }.average()
    return BacktestSummary(results, matched.size, topNHitRate, meanPercentile)
  }
}
